package com.taskflow.auth_service.controller;

import com.taskflow.auth_service.dto.*;
import com.taskflow.auth_service.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/suggest-tasks")
    public ResponseEntity<List<TaskSuggestion>> suggestTasks(@Valid @RequestBody AIRequest request) {
        return ResponseEntity.ok(aiService.suggestTasks(request.getDescription()));
    }
}
