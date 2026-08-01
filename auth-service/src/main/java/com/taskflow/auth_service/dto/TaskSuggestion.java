package com.taskflow.auth_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSuggestion {
    private String title;
    private String priority;
    private String estimate;
}
