package com.taskflow.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.auth_service.dto.TaskRequest;
import com.taskflow.auth_service.dto.TaskResponse;
import com.taskflow.auth_service.enums.Priority;
import com.taskflow.auth_service.enums.TaskStatus;
import com.taskflow.auth_service.service.TaskService;
import com.taskflow.auth_service.filter.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_returns201() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Fix bug");
        request.setPriority(Priority.HIGH);

        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Fix bug")
                .priority(Priority.HIGH)
                .status(TaskStatus.TODO)
                .build();

        when(taskService.create(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/projects/1/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Fix bug"));
    }

    @Test
    void createTask_missingTitle_returns400() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setPriority(Priority.HIGH);

        mockMvc.perform(post("/api/projects/1/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTasks_returns200() throws Exception {
        TaskResponse response = TaskResponse.builder().id(1L).title("Task A").build();
        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.getTasks(eq(1L), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/api/projects/1/tasks/5"))
                .andExpect(status().isNoContent());
    }
}
