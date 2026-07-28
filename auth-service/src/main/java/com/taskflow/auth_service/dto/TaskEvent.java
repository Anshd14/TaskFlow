package com.taskflow.auth_service.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private Long taskId;
    private String eventType;
    private String taskTitle;
    private Long assigneeId;
    private String assigneeEmail;
    private Instant timestamp;
}
