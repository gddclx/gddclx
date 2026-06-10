package com.example.demo.controller;

import com.example.demo.dto.SubmissionResponse;
import com.example.demo.dto.TaskResponse;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务管理 Controller — 全项目端点最多的Controller（12个）
 * 接口路径前缀：/api/task
 * 亮点：抽 ok()/error() 私有方法消除重复代码
 *
 * 管理员操作（7个）：create, list, detail, delete, close, review, submissions
 * 员工操作（5个）：submit, my-submissions, my-submission, update-submission
 *
 * 文件上传使用 MultipartFile[] 接收，UUID重命名存盘，@Transactional保证原子性
 * 级联删除时：物理文件 → 附件表 → 提交表 → 任务表（6层嵌套）
 */
@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    /** 成功响应构建器 */
    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    /** 错误响应构建器 */
    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("success", false);
        map.put("message", msg);
        return map;
    }

    /**
     * 创建任务 — 管理员发布新任务
     * POST /api/task/create (multipart/form-data)
     * @param files 附件（可选，MultipartFile[]接收多个文件）
     * @param columnNo 栏位号（1/2/3，可选，配合三栏看板使用）
     */
    @PostMapping("/create")
    public Map<String, Object> create(
            @RequestParam("publisherId") String publisherId,
            @RequestParam("title") String title,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "columnNo", required = false) Integer columnNo,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {  // 文件数组
        try {
            TaskResponse r = taskService.createTask(publisherId, title, content, deadline, files, columnNo);
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 任务列表 — 支持按状态和栏位过滤
     * GET /api/task/list?filter=active&column=1
     * filter=active 只显示进行中的任务
     * column=N 只显示指定栏位的任务
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "all") String filter,   // "all" 或 "active"
            @RequestParam(required = false) Integer column) {    // 可选：栏位过滤
        try {
            List<TaskResponse> list;
            if (column != null) {
                boolean activeOnly = "active".equals(filter);
                list = taskService.getTasksByColumn(column, activeOnly);
            } else if ("active".equals(filter)) {
                list = taskService.getActiveTasks();
            } else {
                list = taskService.getAllTasks();
            }
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 任务详情（含附件URL和提交人数）
     * GET /api/task/5
     */
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        try {
            TaskResponse r = taskService.getTask(id);
            if (r == null) return error("任务不存在");
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 删除任务 — 级联删除（@Transactional原子操作）
     * DELETE /api/task/5（RESTful标准）
     * 删除顺序：提交附件文件 → 提交附件记录 → 提交记录 → 任务附件文件 → 任务附件记录 → 任务
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 关闭任务 — 不再接受新提交
     * POST /api/task/5/close
     * status: "active" → "closed"
     */
    @PostMapping("/{id}/close")
    public Map<String, Object> close(@PathVariable Long id) {
        try {
            taskService.closeTask(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 员工提交任务 — 状态机驱动
     * POST /api/task/submit (multipart/form-data)
     * 状态机规则：
     * 无记录 → 新建(status=pending)
     * pending → 拒绝（"已提交，等待审核"）
     * approved → 拒绝（"已验收通过"）
     * rejected → 允许重提（更新内容+删旧附件+存新附件，status变回pending）
     */
    @PostMapping("/submit")
    public Map<String, Object> submit(
            @RequestParam("taskId") Long taskId,
            @RequestParam("employeeId") String employeeId,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            SubmissionResponse r = taskService.submitTask(taskId, employeeId, content, files);
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 查看某任务的所有提交 — 管理员视角
     * GET /api/task/submissions/5
     */
    @GetMapping("/submissions/{taskId}")
    public Map<String, Object> taskSubmissions(@PathVariable Long taskId) {
        try {
            List<SubmissionResponse> list = taskService.getSubmissions(taskId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 我的所有提交记录 — 员工视角
     * GET /api/task/my-submissions?employeeId=A002
     */
    @GetMapping("/my-submissions")
    public Map<String, Object> mySubmissions(@RequestParam String employeeId) {
        try {
            List<SubmissionResponse> list = taskService.getMySubmissions(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 审核提交 — 管理员通过或驳回
     * POST /api/task/review/3?status=approved&comment=做的不错
     * @param status "approved"(通过) 或 "rejected"(驳回)
     * @param comment 审核评语（可选）
     */
    @PostMapping("/review/{submissionId}")
    public Map<String, Object> review(@PathVariable Long submissionId,
                                       @RequestParam String status,
                                       @RequestParam(value = "comment", defaultValue = "") String comment) {
        try {
            taskService.reviewSubmission(submissionId, status, comment);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 我对某任务的提交状态 — 员工查看自己是否已提交
     * GET /api/task/5/my-submission?employeeId=A002
     */
    @GetMapping("/{taskId}/my-submission")
    public Map<String, Object> mySubmission(@PathVariable Long taskId, @RequestParam String employeeId) {
        try {
            SubmissionResponse r = taskService.getMySubmission(taskId, employeeId);
            return ok(r);  // 未提交返回null
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 更新被驳回的提交（重新提交）
     * POST /api/task/submit/3/update (multipart/form-data)
     * 更新内容 + 删旧附件物理文件 + 存新附件
     */
    @PostMapping("/submit/{submissionId}/update")
    public Map<String, Object> updateSubmission(@PathVariable Long submissionId,
                                                @RequestParam(value = "content", required = false, defaultValue = "") String content,
                                                @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            SubmissionResponse r = taskService.resubmitTask(submissionId, content, files);
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
