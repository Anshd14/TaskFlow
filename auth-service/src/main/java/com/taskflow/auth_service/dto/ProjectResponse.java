package com.taskflow.auth_service.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private Long workspaceId;
    private Instant createdAt;
}
