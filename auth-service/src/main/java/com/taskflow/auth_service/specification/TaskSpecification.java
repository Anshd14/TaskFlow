package com.taskflow.auth_service.specification;

import com.taskflow.auth_service.entity.Task;
import com.taskflow.auth_service.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> withFilters(Long projectId, TaskStatus status, Long assigneeId) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (projectId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("project").get("id"), projectId));
            }
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (assigneeId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("assignee").get("id"), assigneeId));
            }

            return predicates;
        };
    }
}
