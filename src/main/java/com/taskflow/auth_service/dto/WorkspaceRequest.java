package com.taskflow.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkspaceRequest {

    @NotBlank
    private String name;
}
