package com.taskmanager.service;

import com.taskmanager.dto.DashboardSummary;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks(String status, String search);

    Task getTaskById(Long id);

    Task createTask(TaskRequest request);

    Task updateTask(Long id, TaskRequest request);

    Task updateTaskStatus(Long id, String status);

    void deleteTask(Long id);

    DashboardSummary getDashboardSummary();
}
