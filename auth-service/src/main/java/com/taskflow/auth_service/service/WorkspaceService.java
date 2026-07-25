package com.taskflow.auth_service.service;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.entity.User;
import com.taskflow.auth_service.entity.Workspace;
import com.taskflow.auth_service.repository.UserRepository;
import com.taskflow.auth_service.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceResponse create(WorkspaceRequest request, String userEmail) {
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .owner(owner)
                .build();

        workspaceRepository.save(workspace);

        return toResponse(workspace);
    }

    public List<WorkspaceResponse> getAll(String userEmail) {
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return workspaceRepository.findByOwnerId(owner.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WorkspaceResponse getById(Long id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
        return toResponse(workspace);
    }

    public void delete(Long id) {
        if (!workspaceRepository.existsById(id)) {
            throw new RuntimeException("Workspace not found");
        }
        workspaceRepository.deleteById(id);
    }

    private WorkspaceResponse toResponse(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .ownerEmail(workspace.getOwner().getEmail())
                .createdAt(workspace.getCreatedAt())
                .build();
    }
}
