package com.taskflow.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AIRequest {

    @NotBlank
    private String description;
}
