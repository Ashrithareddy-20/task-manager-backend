package com.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskStatusUpdate;
import com.taskmanager.entity.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DailyTaskManagerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void testCreateTask_Success() throws Exception {
        TaskRequest request = new TaskRequest("Test Task", "Test Description", LocalDate.now().plusDays(1), "Pending");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("Pending"));
    }

    @Test
    void testCreateTask_ValidationError_BlankTitle() throws Exception {
        TaskRequest request = new TaskRequest("", "No title description", LocalDate.now(), "Pending");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Validation failed")));
    }

    @Test
    void testGetAllTasks() throws Exception {
        taskRepository.save(new Task("Task 1", "Desc 1", LocalDate.now(), "Pending"));
        taskRepository.save(new Task("Task 2", "Desc 2", LocalDate.now(), "Completed"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testFilterTasksByStatus() throws Exception {
        taskRepository.save(new Task("Task 1", "Desc 1", LocalDate.now(), "Pending"));
        taskRepository.save(new Task("Task 2", "Desc 2", LocalDate.now(), "Completed"));

        mockMvc.perform(get("/api/tasks?status=Pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Task 1"));

        mockMvc.perform(get("/api/tasks?status=Completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Task 2"));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task saved = taskRepository.save(new Task("Original Title", "Desc", LocalDate.now(), "Pending"));

        TaskRequest updateReq = new TaskRequest("Updated Title", "Updated Desc", LocalDate.now().plusDays(3), "Completed");

        mockMvc.perform(put("/api/tasks/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.status").value("Completed"));
    }

    @Test
    void testPatchTaskStatus() throws Exception {
        Task saved = taskRepository.save(new Task("Task to complete", "Desc", LocalDate.now(), "Pending"));

        TaskStatusUpdate statusUpdate = new TaskStatusUpdate("Completed");

        mockMvc.perform(patch("/api/tasks/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Completed"));
    }

    @Test
    void testDeleteTask() throws Exception {
        Task saved = taskRepository.save(new Task("Task to delete", "Desc", LocalDate.now(), "Pending"));

        mockMvc.perform(delete("/api/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/tasks/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDashboardSummary() throws Exception {
        taskRepository.save(new Task("Task 1", "Desc 1", LocalDate.now(), "Pending"));
        taskRepository.save(new Task("Task 2", "Desc 2", LocalDate.now(), "Pending"));
        taskRepository.save(new Task("Task 3", "Desc 3", LocalDate.now(), "Completed"));

        mockMvc.perform(get("/api/tasks/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(3))
                .andExpect(jsonPath("$.pendingTasks").value(2))
                .andExpect(jsonPath("$.completedTasks").value(1));
    }
}
