package com.taskflow.auth_service.service;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.entity.Project;
import com.taskflow.auth_service.entity.Task;
import com.taskflow.auth_service.entity.User;
import com.taskflow.auth_service.enums.TaskStatus;
import com.taskflow.auth_service.repository.ProjectRepository;
import com.taskflow.auth_service.repository.TaskRepository;
import com.taskflow.auth_service.repository.UserRepository;
import com.taskflow.auth_service.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskEventPublisher taskEventPublisher;

    public TaskResponse create(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .project(project)
                .assignee(assignee)
                .build();

        taskRepository.save(task);

        taskEventPublisher.publishTaskCreated(task);
        if (assignee != null) {
            taskEventPublisher.publishTaskAssigned(task);
        }

        return toResponse(task);
    }

    public Page<TaskResponse> getTasks(Long projectId, TaskStatus status, Long assigneeId, Pageable pageable) {
        var spec = TaskSpecification.withFilters(projectId, status, assigneeId);
        return taskRepository.findAll(spec, pageable).map(this::toResponse);
    }

    public TaskResponse updateStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        taskRepository.save(task);
        return toResponse(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .projectId(task.getProject().getId())
                .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
                .createdAt(task.getCreatedAt())
                .build();
    }
}
