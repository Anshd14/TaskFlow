package com.taskflow.auth_service.service;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.entity.Project;
import com.taskflow.auth_service.entity.Workspace;
import com.taskflow.auth_service.repository.ProjectRepository;
import com.taskflow.auth_service.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;

    public ProjectResponse create(Long workspaceId, ProjectRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .workspace(workspace)
                .build();

        projectRepository.save(project);
        return toResponse(project);
    }

    public List<ProjectResponse> getByWorkspace(Long workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return toResponse(project);
    }

    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .workspaceId(project.getWorkspace().getId())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
