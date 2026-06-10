package com.example.demo.service.impl;

import com.example.demo.domain.*;
import com.example.demo.dto.SubmissionResponse;
import com.example.demo.dto.TaskResponse;
import com.example.demo.mapper.*;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务服务实现类 — 全项目最复杂的Service
 * 依赖4个Mapper：TaskMapper, TaskFileMapper, TaskSubmissionMapper, SubmissionFileMapper
 *
 * 核心功能：
 * 1. 任务创建 + 多文件上传（UUID重命名防冲突）
 * 2. 员工提交 — 状态机（pending/approved/rejected）
 * 3. 管理员审核（通过/驳回+评语）
 * 4. 级联删除 — @Transactional 原子操作（物理文件+数据库6层删除）
 * 5. 驳回重提 — 更新内容 + 删旧文件 + 存新文件
 *
 * 文件存储：@Value("${app.upload.dir}")/tasks/ 或 submissions/ 子目录
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired private TaskMapper taskMapper;
    @Autowired private TaskFileMapper taskFileMapper;
    @Autowired private TaskSubmissionMapper taskSubmissionMapper;
    @Autowired private SubmissionFileMapper submissionFileMapper;

    @Value("${app.upload.dir:uploads}")  // 文件上传根目录
    private String uploadDir;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 创建任务 — @Transactional保证任务INSERT + 附件INSERT原子性
     * 附件保存流程：原始文件名 → UUID重命名 → 存到 uploads/tasks/ → DB记录原始名+UUID路径
     */
    @Override
    @Transactional  // 任务插入 + 附件插入 = 一个事务
    public TaskResponse createTask(String publisherId, String title, String content,
                                    String deadline, MultipartFile[] files, Integer columnNo) {
        Task task = new Task();
        task.setPublisherId(publisherId);
        task.setTitle(title);
        task.setContent(content);
        task.setDeadline(deadline != null && !deadline.isEmpty() ? LocalDate.parse(deadline, DATE_FMT) : null);
        task.setColumnNo(columnNo);
        taskMapper.insert(task);  // MyBatis回填自增ID

        // 保存附件
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String savedPath = saveFile(file, "tasks");  // UUID重命名存盘
                TaskFile tf = new TaskFile();
                tf.setTaskId(task.getId());  // 外键 → task表
                tf.setFileName(file.getOriginalFilename());  // 原始名
                tf.setFilePath(savedPath);   // UUID路径
                tf.setFileSize(file.getSize());
                taskFileMapper.insert(tf);
            }
        }

        return buildTaskResponse(task);
    }

    @Override public TaskResponse getTask(Long taskId) {
        Task task = taskMapper.findById(taskId);
        return task != null ? buildTaskResponse(task) : null;
    }

    @Override public List<TaskResponse> getActiveTasks() {
        return taskMapper.findActive().stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override public List<TaskResponse> getTasksByColumn(Integer columnNo, boolean activeOnly) {
        List<Task> list;
        if (columnNo == null || columnNo == 0) list = taskMapper.findUncategorized();
        else if (activeOnly) list = taskMapper.findByColumn(columnNo);
        else list = taskMapper.findByColumnAll(columnNo);
        return list.stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override public List<TaskResponse> getAllTasks() {
        return taskMapper.findAll().stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override public void closeTask(Long taskId) { taskMapper.closeTask(taskId); }

    /**
     * 级联删除任务 — @Transactional保护，6层删除
     * 顺序：提交附件物理文件 → 提交附件记录 → 提交记录 → 任务附件物理文件 → 任务附件记录 → 任务
     * 任何一步失败，整个事务回滚
     */
    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        // 第1步：遍历该任务的所有提交
        List<TaskSubmission> subs = taskSubmissionMapper.findByTaskId(taskId);
        for (TaskSubmission sub : subs) {
            // 第2步：删提交附件（物理文件 + 数据库）
            List<SubmissionFile> subFiles = submissionFileMapper.findBySubmissionId(sub.getId());
            for (SubmissionFile f : subFiles) {
                deletePhysically(f.getFilePath());  // 物理删除
            }
            submissionFileMapper.deleteBySubmissionId(sub.getId());  // 数据库删除
        }
        // 第3步：删提交记录
        taskSubmissionMapper.deleteByTaskId(taskId);

        // 第4步：删任务附件（物理文件 + 数据库）
        List<TaskFile> taskFiles = taskFileMapper.findByTaskId(taskId);
        for (TaskFile f : taskFiles) {
            deletePhysically(f.getFilePath());
        }
        taskFileMapper.deleteByTaskId(taskId);

        // 第5步：删任务本身
        taskMapper.deleteById(taskId);
    }

    /**
     * 员工提交任务 — 状态机
     * 无记录 → INSERT (pending)
     * pending → 抛异常
     * approved → 抛异常
     * rejected → 更新内容 + 删旧附件 + 存新附件 (→ pending)
     */
    @Override
    @Transactional
    public SubmissionResponse submitTask(Long taskId, String employeeId, String content, MultipartFile[] files) {
        // 状态检查
        TaskSubmission existing = taskSubmissionMapper.findByTaskIdAndEmployeeId(taskId, employeeId);
        if (existing != null) {
            if ("pending".equals(existing.getStatus())) {
                throw new RuntimeException("你已经提交过了，请等待管理员审核");
            }
            if ("approved".equals(existing.getStatus())) {
                throw new RuntimeException("该任务已验收通过，无需重复提交");
            }
            // rejected → 允许重提：更新内容 + 删旧文件 + 存新文件
            existing.setContent(content);
            existing.setStatus("pending");
            existing.setReviewComment(null);
            taskSubmissionMapper.updateContent(existing.getId(), content);

            // 删除旧附件
            List<SubmissionFile> oldFiles = submissionFileMapper.findBySubmissionId(existing.getId());
            for (SubmissionFile f : oldFiles) {
                deletePhysically(f.getFilePath());
                submissionFileMapper.deleteById(f.getId());
            }

            // 保存新附件
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;
                    String savedPath = saveFile(file, "submissions");
                    SubmissionFile sf = new SubmissionFile();
                    sf.setSubmissionId(existing.getId());
                    sf.setFileName(file.getOriginalFilename());
                    sf.setFilePath(savedPath);
                    sf.setFileSize(file.getSize());
                    submissionFileMapper.insert(sf);
                }
            }
            return buildSubmissionResponse(existing);
        }

        // 首次提交
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(taskId);
        submission.setEmployeeId(employeeId);
        submission.setContent(content);
        taskSubmissionMapper.insert(submission);

        // 保存附件
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String savedPath = saveFile(file, "submissions");
                SubmissionFile sf = new SubmissionFile();
                sf.setSubmissionId(submission.getId());
                sf.setFileName(file.getOriginalFilename());
                sf.setFilePath(savedPath);
                sf.setFileSize(file.getSize());
                submissionFileMapper.insert(sf);
            }
        }

        return buildSubmissionResponse(submission);
    }

    @Override public List<SubmissionResponse> getSubmissions(Long taskId) {
        return taskSubmissionMapper.findByTaskId(taskId).stream()
                .map(this::buildSubmissionResponse).collect(Collectors.toList());
    }

    @Override public List<SubmissionResponse> getMySubmissions(String employeeId) {
        return taskSubmissionMapper.findByEmployeeId(employeeId).stream()
                .map(this::buildSubmissionResponse).collect(Collectors.toList());
    }

    /** 审核提交 — 管理员通过/驳回 */
    @Override
    public void reviewSubmission(Long submissionId, String status, String reviewComment) {
        taskSubmissionMapper.updateStatus(submissionId, status, reviewComment);
    }

    @Override
    public SubmissionResponse getMySubmission(Long taskId, String employeeId) {
        TaskSubmission sub = taskSubmissionMapper.findByTaskIdAndEmployeeId(taskId, employeeId);
        return sub != null ? buildSubmissionResponse(sub) : null;
    }

    /** 重新提交 — 更新内容 + 删旧附件 + 存新附件 */
    @Override
    @Transactional
    public SubmissionResponse resubmitTask(Long submissionId, String content, MultipartFile[] files) {
        taskSubmissionMapper.updateContent(submissionId, content);
        // 删旧附件
        List<SubmissionFile> oldFiles = submissionFileMapper.findBySubmissionId(submissionId);
        for (SubmissionFile f : oldFiles) {
            deletePhysically(f.getFilePath());
            submissionFileMapper.deleteById(f.getId());
        }
        // 存新附件
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String savedPath = saveFile(file, "submissions");
                SubmissionFile sf = new SubmissionFile();
                sf.setSubmissionId(submissionId);
                sf.setFileName(file.getOriginalFilename());
                sf.setFilePath(savedPath);
                sf.setFileSize(file.getSize());
                submissionFileMapper.insert(sf);
            }
        }
        TaskSubmission sub = taskSubmissionMapper.findById(submissionId);
        return buildSubmissionResponse(sub);
    }

    // ====================== 文件处理工具方法 ======================

    /**
     * 保存上传文件 — UUID重命名防冲突
     * @param file 上传文件
     * @param subDir 子目录名（"tasks" 或 "submissions"）
     * @return 相对路径 "tasks/UUID.docx"
     */
    private String saveFile(MultipartFile file, String subDir) {
        String originalName = file.getOriginalFilename();
        String ext = "";  // 扩展名
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;  // "a3f8c2b1-xxxx-xxxx.docx"
        String dirPath = uploadDir + File.separator + subDir;     // "uploads/tasks"
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();  // 目录不存在则创建

        try {
            File dest = new File(dir, storedName);
            file.transferTo(dest);  // Spring MultipartFile → 本地文件
            return subDir + "/" + storedName;  // "tasks/UUID.docx"
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /** 物理删除磁盘文件（只在@Transactional方法内调用） */
    private void deletePhysically(String filePath) {
        if (filePath == null) return;
        File file = new File(uploadDir, filePath);
        if (file.exists()) file.delete();
    }

    // ====================== DTO构建方法 ======================

    /** 构建任务响应DTO — 含附件列表和提交人数 */
    private TaskResponse buildTaskResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setPublisherId(task.getPublisherId());
        r.setTitle(task.getTitle());
        r.setContent(task.getContent());
        r.setDeadline(task.getDeadline() != null ? task.getDeadline().toString() : null);
        r.setStatus(task.getStatus());
        r.setColumnNo(task.getColumnNo());
        r.setCreatedAt(task.getCreatedAt() != null ? task.getCreatedAt().toString() : null);

        // 组装附件信息（文件名 + 访问URL）
        List<TaskFile> files = taskFileMapper.findByTaskId(task.getId());
        r.setFiles(files.stream().map(f -> {
            TaskResponse.FileInfo fi = new TaskResponse.FileInfo();
            fi.setId(f.getId());
            fi.setFileName(f.getFileName());
            fi.setUrl("/uploads/" + f.getFilePath());  // 前端可直接访问的URL
            fi.setFileSize(f.getFileSize());
            return fi;
        }).collect(Collectors.toList()));

        // 统计提交人数
        List<TaskSubmission> subs = taskSubmissionMapper.findByTaskId(task.getId());
        r.setSubmissionCount(subs.size());

        return r;
    }

    /** 构建提交响应DTO — 含附件列表和审核状态 */
    private SubmissionResponse buildSubmissionResponse(TaskSubmission sub) {
        SubmissionResponse r = new SubmissionResponse();
        r.setId(sub.getId());
        r.setTaskId(sub.getTaskId());
        r.setEmployeeId(sub.getEmployeeId());
        r.setContent(sub.getContent());
        r.setStatus(sub.getStatus());
        r.setReviewComment(sub.getReviewComment());
        r.setCreatedAt(sub.getCreatedAt() != null ? sub.getCreatedAt().toString() : null);

        List<SubmissionFile> files = submissionFileMapper.findBySubmissionId(sub.getId());
        r.setFiles(files.stream().map(f -> {
            TaskResponse.FileInfo fi = new TaskResponse.FileInfo();
            fi.setId(f.getId());
            fi.setFileName(f.getFileName());
            fi.setUrl("/uploads/" + f.getFilePath());
            fi.setFileSize(f.getFileSize());
            return fi;
        }).collect(Collectors.toList()));

        return r;
    }
}
