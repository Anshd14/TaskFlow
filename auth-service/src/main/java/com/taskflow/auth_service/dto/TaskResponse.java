package com.taskflow.auth_service.dto;

import com.taskflow.auth_service.enums.Priority;
import com.taskflow.auth_service.enums.TaskStatus;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Long projectId;
    private String assigneeEmail;
    private Instant createdAt;
}
