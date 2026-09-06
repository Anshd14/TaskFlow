package com.taskflow.auth_service.service;

import com.taskflow.auth_service.dto.AttachmentResponse;
import com.taskflow.auth_service.entity.Attachment;
import com.taskflow.auth_service.entity.Task;
import com.taskflow.auth_service.repository.AttachmentRepository;
import com.taskflow.auth_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final S3Service s3Service;

    public AttachmentResponse upload(Long taskId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        String key = s3Service.uploadFile(file, taskId);
        String url = s3Service.getFileUrl(key);

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl(url)
                .s3Key(key)
                .task(task)
                .build();

        attachmentRepository.save(attachment);
        return toResponse(attachment);
    }

    public List<AttachmentResponse> getByTask(Long taskId) {
        return attachmentRepository.findByTaskId(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        s3Service.deleteFile(attachment.getS3Key());
        attachmentRepository.delete(attachment);
    }

    private AttachmentResponse toResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}
