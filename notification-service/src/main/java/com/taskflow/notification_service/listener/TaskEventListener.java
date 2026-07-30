package com.taskflow.notification_service.listener;

import com.taskflow.notification_service.dto.TaskEvent;
import com.taskflow.notification_service.entity.Notification;
import com.taskflow.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventListener {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "task.assigned", groupId = "notification-group")
    public void handleTaskAssigned(TaskEvent event) {
        if (event.getAssigneeId() == null) return;

        Notification notification = Notification.builder()
                .userId(event.getAssigneeId())
                .message("Task \"" + event.getTaskTitle() + "\" was assigned to you")
                .build();

        notificationRepository.save(notification);
    }

    @KafkaListener(topics = "task.created", groupId = "notification-group")
    public void handleTaskCreated(TaskEvent event) {
        System.out.println("New task created: " + event.getTaskTitle());
    }
}
