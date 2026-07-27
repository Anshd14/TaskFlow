package com.taskflow.auth_service.dto;

import com.taskflow.auth_service.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Priority priority;

    private Long assigneeId;
}
