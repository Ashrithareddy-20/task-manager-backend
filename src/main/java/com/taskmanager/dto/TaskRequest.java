package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TaskRequest {

    @NotBlank(message = "Task title is required and cannot be empty")
    @Size(max = 255, message = "Task title cannot exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
    private String description;

    private LocalDate dueDate;

    @Pattern(regexp = "^(?i)(Pending|Completed)?$", message = "Task status must be either 'Pending' or 'Completed'")
    private String status;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, LocalDate dueDate, String status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
