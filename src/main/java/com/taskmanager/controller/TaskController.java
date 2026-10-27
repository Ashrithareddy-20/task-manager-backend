package com.taskmanager.controller;

import com.taskmanager.dto.DashboardSummary;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskStatusUpdate;
import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /api/tasks - Retrieve all tasks, with optional status and search filtering
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<Task> tasks = taskService.getAllTasks(status, search);
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/summary - Retrieve dashboard counters (total, pending, completed)
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        DashboardSummary summary = taskService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/tasks/{id} - Retrieve a specific task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * POST /api/tasks - Create a new task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request) {
        Task createdTask = taskService.createTask(request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * PUT /api/tasks/{id} - Update an existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        Task updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * PATCH /api/tasks/{id}/status - Update task status (Pending / Completed)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdate statusUpdate) {
        Task updatedTask = taskService.updateTaskStatus(id, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * DELETE /api/tasks/{id} - Delete a task by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task with ID " + id + " has been successfully deleted");
        response.put("deletedTaskId", id);
        return ResponseEntity.ok(response);
    }
}
