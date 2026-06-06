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

@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("success", false);
        map.put("message", msg);
        return map;
    }

    @PostMapping("/create")
    public Map<String, Object> create(
            @RequestParam("publisherId") String publisherId,
            @RequestParam("title") String title,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "columnNo", required = false) Integer columnNo,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            TaskResponse r = taskService.createTask(publisherId, title, content, deadline, files, columnNo);
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(required = false) Integer column) {
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

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/{id}/close")
    public Map<String, Object> close(@PathVariable Long id) {
        try {
            taskService.closeTask(id);
            return ok(null);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

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

    @GetMapping("/submissions/{taskId}")
    public Map<String, Object> taskSubmissions(@PathVariable Long taskId) {
        try {
            List<SubmissionResponse> list = taskService.getSubmissions(taskId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/my-submissions")
    public Map<String, Object> mySubmissions(@RequestParam String employeeId) {
        try {
            List<SubmissionResponse> list = taskService.getMySubmissions(employeeId);
            return ok(list);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

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

    @GetMapping("/{taskId}/my-submission")
    public Map<String, Object> mySubmission(@PathVariable Long taskId, @RequestParam String employeeId) {
        try {
            SubmissionResponse r = taskService.getMySubmission(taskId, employeeId);
            return ok(r);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

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
