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

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskFileMapper taskFileMapper;
    @Autowired
    private TaskSubmissionMapper taskSubmissionMapper;
    @Autowired
    private SubmissionFileMapper submissionFileMapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public TaskResponse createTask(String publisherId, String title, String content, String deadline, MultipartFile[] files, Integer columnNo) {
        Task task = new Task();
        task.setPublisherId(publisherId);
        task.setTitle(title);
        task.setContent(content);
        task.setDeadline(deadline != null && !deadline.isEmpty() ? LocalDate.parse(deadline, DATE_FMT) : null);
        task.setColumnNo(columnNo);
        taskMapper.insert(task);

        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String savedPath = saveFile(file, "tasks");
                TaskFile tf = new TaskFile();
                tf.setTaskId(task.getId());
                tf.setFileName(file.getOriginalFilename());
                tf.setFilePath(savedPath);
                tf.setFileSize(file.getSize());
                taskFileMapper.insert(tf);
            }
        }

        return buildTaskResponse(task);
    }

    @Override
    public TaskResponse getTask(Long taskId) {
        Task task = taskMapper.findById(taskId);
        return task != null ? buildTaskResponse(task) : null;
    }

    @Override
    public List<TaskResponse> getActiveTasks() {
        return taskMapper.findActive().stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByColumn(Integer columnNo, boolean activeOnly) {
        List<Task> list;
        if (columnNo == null || columnNo == 0) {
            list = taskMapper.findUncategorized();
        } else if (activeOnly) {
            list = taskMapper.findByColumn(columnNo);
        } else {
            list = taskMapper.findByColumnAll(columnNo);
        }
        return list.stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskMapper.findAll().stream().map(this::buildTaskResponse).collect(Collectors.toList());
    }

    @Override
    public void closeTask(Long taskId) {
        taskMapper.closeTask(taskId);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        // 级联删除：提交附件文件 → 提交附件记录 → 提交记录 → 任务附件文件 → 任务附件记录 → 任务
        List<TaskSubmission> subs = taskSubmissionMapper.findByTaskId(taskId);
        for (TaskSubmission sub : subs) {
            List<SubmissionFile> subFiles = submissionFileMapper.findBySubmissionId(sub.getId());
            for (SubmissionFile f : subFiles) {
                deletePhysically(f.getFilePath());
            }
            submissionFileMapper.deleteBySubmissionId(sub.getId());
        }
        taskSubmissionMapper.deleteByTaskId(taskId);
        List<TaskFile> taskFiles = taskFileMapper.findByTaskId(taskId);
        for (TaskFile f : taskFiles) {
            deletePhysically(f.getFilePath());
        }
        taskFileMapper.deleteByTaskId(taskId);
        taskMapper.deleteById(taskId);
    }

    @Override
    @Transactional
    public SubmissionResponse submitTask(Long taskId, String employeeId, String content, MultipartFile[] files) {
        // 检查是否已提交
        TaskSubmission existing = taskSubmissionMapper.findByTaskIdAndEmployeeId(taskId, employeeId);
        if (existing != null) {
            if ("pending".equals(existing.getStatus())) {
                throw new RuntimeException("你已经提交过了，请等待管理员审核");
            }
            if ("approved".equals(existing.getStatus())) {
                throw new RuntimeException("该任务已验收通过，无需重复提交");
            }
            // rejected → 允许重新提交（更新现有记录）
            existing.setContent(content);
            existing.setStatus("pending");
            existing.setReviewComment(null);
            taskSubmissionMapper.updateContent(existing.getId(), content);
            // 删除旧附件（物理文件 + 数据库）
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

        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(taskId);
        submission.setEmployeeId(employeeId);
        submission.setContent(content);
        taskSubmissionMapper.insert(submission);

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

    @Override
    public List<SubmissionResponse> getSubmissions(Long taskId) {
        return taskSubmissionMapper.findByTaskId(taskId).stream()
                .map(this::buildSubmissionResponse).collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getMySubmissions(String employeeId) {
        return taskSubmissionMapper.findByEmployeeId(employeeId).stream()
                .map(this::buildSubmissionResponse).collect(Collectors.toList());
    }

    @Override
    public void reviewSubmission(Long submissionId, String status, String reviewComment) {
        taskSubmissionMapper.updateStatus(submissionId, status, reviewComment);
    }

    @Override
    public SubmissionResponse getMySubmission(Long taskId, String employeeId) {
        TaskSubmission sub = taskSubmissionMapper.findByTaskIdAndEmployeeId(taskId, employeeId);
        return sub != null ? buildSubmissionResponse(sub) : null;
    }

    @Override
    @Transactional
    public SubmissionResponse resubmitTask(Long submissionId, String content, MultipartFile[] files) {
        taskSubmissionMapper.updateContent(submissionId, content);
        // 删除旧附件（物理文件 + 数据库）
        List<SubmissionFile> oldFiles = submissionFileMapper.findBySubmissionId(submissionId);
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

    private String saveFile(MultipartFile file, String subDir) {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        String dirPath = uploadDir + File.separator + subDir;
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        try {
            File dest = new File(dir, storedName);
            file.transferTo(dest);
            return subDir + "/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    private void deletePhysically(String filePath) {
        if (filePath == null) return;
        File file = new File(uploadDir, filePath);
        if (file.exists()) file.delete();
    }

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

        List<TaskFile> files = taskFileMapper.findByTaskId(task.getId());
        r.setFiles(files.stream().map(f -> {
            TaskResponse.FileInfo fi = new TaskResponse.FileInfo();
            fi.setId(f.getId());
            fi.setFileName(f.getFileName());
            fi.setUrl("/uploads/" + f.getFilePath());
            fi.setFileSize(f.getFileSize());
            return fi;
        }).collect(Collectors.toList()));

        List<TaskSubmission> subs = taskSubmissionMapper.findByTaskId(task.getId());
        r.setSubmissionCount(subs.size());

        return r;
    }

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
