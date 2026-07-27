package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.enums.TaskStatus;
import com.taskflow.auth_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(201).body(taskService.create(projectId, request));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assigneeId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(projectId, status, assigneeId, pageable));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
