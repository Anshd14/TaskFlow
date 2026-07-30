package com.taskflow.notification_service.dto;

import lombok.*;

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
    private Long timestamp;
}
