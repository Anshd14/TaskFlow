package com.taskflow.auth_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.auth_service.dto.TaskSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    public List<TaskSuggestion> suggestTasks(String description) {
        ChatClient chatClient = chatClientBuilder.build();

        String prompt = """
                Given this project description: %s

                Generate exactly 5 specific development tasks as a JSON array.
                Each object must have exactly these fields: "title", "priority" (HIGH, MEDIUM, or LOW), "estimate" (like "2h", "1d").

                Return ONLY the raw JSON array, no explanation, no markdown formatting, no code blocks.
                Example format:
                [{"title":"Design database schema","priority":"HIGH","estimate":"3h"}]
                """.formatted(description);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        try {
            String cleaned = response.trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            TaskSuggestion[] suggestions = objectMapper.readValue(cleaned, TaskSuggestion[].class);
            return List.of(suggestions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }
}
