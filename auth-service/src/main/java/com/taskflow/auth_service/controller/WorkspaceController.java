package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(
            @Valid @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(201)
                .body(workspaceService.create(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(workspaceService.getAll(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workspaceService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workspaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
