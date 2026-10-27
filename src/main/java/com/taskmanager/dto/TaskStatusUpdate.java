package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TaskStatusUpdate {

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(?i)(Pending|Completed)$", message = "Status must be either 'Pending' or 'Completed'")
    private String status;

    public TaskStatusUpdate() {
    }

    public TaskStatusUpdate(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
