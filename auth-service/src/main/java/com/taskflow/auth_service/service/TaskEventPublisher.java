package com.taskflow.auth_service.service;

import com.taskflow.auth_service.dto.TaskEvent;
import com.taskflow.auth_service.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private static final String TOPIC_CREATED = "task.created";
    private static final String TOPIC_ASSIGNED = "task.assigned";

    public void publishTaskCreated(Task task) {
        TaskEvent event = buildEvent(task, "CREATED");
        kafkaTemplate.send(TOPIC_CREATED, task.getId().toString(), event);
    }

    public void publishTaskAssigned(Task task) {
        if (task.getAssignee() == null) return;
        TaskEvent event = buildEvent(task, "ASSIGNED");
        kafkaTemplate.send(TOPIC_ASSIGNED, task.getId().toString(), event);
    }

    private TaskEvent buildEvent(Task task, String eventType) {
        return TaskEvent.builder()
                .taskId(task.getId())
                .eventType(eventType)
                .taskTitle(task.getTitle())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
                .timestamp(Instant.now())
                .build();
    }
}
