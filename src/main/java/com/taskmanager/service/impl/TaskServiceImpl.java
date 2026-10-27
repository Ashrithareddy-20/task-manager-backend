package com.taskmanager.service.impl;

import com.taskmanager.dto.DashboardSummary;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks(String status, String search) {
        if (search != null && !search.trim().isEmpty()) {
            List<Task> searchResults = taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search.trim(), search.trim()
            );
            if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
                String normalizedStatus = normalizeStatus(status);
                return searchResults.stream()
                        .filter(t -> t.getStatus().equalsIgnoreCase(normalizedStatus))
                        .toList();
            }
            return searchResults;
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            return taskRepository.findByStatusIgnoreCase(normalizeStatus(status));
        }

        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    @Override
    public Task createTask(TaskRequest request) {
        String normalizedStatus = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? normalizeStatus(request.getStatus())
                : "Pending";

        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setDueDate(request.getDueDate());
        task.setStatus(normalizedStatus);

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskRequest request) {
        Task existing = getTaskById(id);

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        existing.setDueDate(request.getDueDate());

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            existing.setStatus(normalizeStatus(request.getStatus()));
        }

        return taskRepository.save(existing);
    }

    @Override
    public Task updateTaskStatus(Long id, String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        String normalized = normalizeStatus(status);
        if (!normalized.equalsIgnoreCase("Pending") && !normalized.equalsIgnoreCase("Completed")) {
            throw new IllegalArgumentException("Invalid status: '" + status + "'. Allowed values are 'Pending' or 'Completed'");
        }

        Task existing = getTaskById(id);
        existing.setStatus(normalized);
        return taskRepository.save(existing);
    }

    @Override
    public void deleteTask(Long id) {
        Task existing = getTaskById(id);
        taskRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary() {
        long total = taskRepository.count();
        long pending = taskRepository.countByStatusIgnoreCase("Pending");
        long completed = taskRepository.countByStatusIgnoreCase("Completed");
        return new DashboardSummary(total, pending, completed);
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "Pending";
        }
        String clean = status.trim().toLowerCase(Locale.ROOT);
        if (clean.equals("completed")) {
            return "Completed";
        }
        return "Pending";
    }
}
