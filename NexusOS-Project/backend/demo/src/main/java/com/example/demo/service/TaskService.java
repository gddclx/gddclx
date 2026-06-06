package com.example.demo.service;

import com.example.demo.dto.SubmissionResponse;
import com.example.demo.dto.TaskResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(String publisherId, String title, String content, String deadline, MultipartFile[] files, Integer columnNo);
    TaskResponse getTask(Long taskId);
    List<TaskResponse> getActiveTasks();
    List<TaskResponse> getTasksByColumn(Integer columnNo, boolean activeOnly);
    List<TaskResponse> getAllTasks();
    void closeTask(Long taskId);
    void deleteTask(Long taskId);
    SubmissionResponse submitTask(Long taskId, String employeeId, String content, MultipartFile[] files);
    List<SubmissionResponse> getSubmissions(Long taskId);
    List<SubmissionResponse> getMySubmissions(String employeeId);
    void reviewSubmission(Long submissionId, String status, String reviewComment);
    SubmissionResponse getMySubmission(Long taskId, String employeeId);
    SubmissionResponse resubmitTask(Long submissionId, String content, MultipartFile[] files);
}
