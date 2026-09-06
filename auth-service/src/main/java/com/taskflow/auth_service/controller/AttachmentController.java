package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.AttachmentResponse;
import com.taskflow.auth_service.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AttachmentResponse> upload(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201).body(attachmentService.upload(taskId, file));
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAll(@PathVariable Long taskId) {
        return ResponseEntity.ok(attachmentService.getByTask(taskId));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        attachmentService.delete(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
