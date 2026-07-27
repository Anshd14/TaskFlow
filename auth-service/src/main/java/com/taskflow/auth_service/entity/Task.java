package com.taskflow.auth_service.entity;

import com.taskflow.auth_service.enums.Priority;
import com.taskflow.auth_service.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "tasks", indexes = {
        @Index(columnList = "project_id"),
        @Index(columnList = "assignee_id"),
        @Index(columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = TaskStatus.TODO;
        }
    }
}
