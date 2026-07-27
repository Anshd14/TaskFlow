package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @PathVariable Long workspaceId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(201).body(projectService.create(workspaceId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(projectService.getByWorkspace(workspaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long workspaceId, @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long workspaceId, @PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
