-- Daily Task Manager Database Schema (MySQL)

CREATE DATABASE IF NOT EXISTS task_manager_db;
USE task_manager_db;

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sample initial tasks (optional)
INSERT INTO tasks (title, description, due_date, status, created_at, updated_at)
SELECT 'Complete Project Proposal', 'Prepare and review the task manager project documentation', CURRENT_DATE + INTERVAL 1 DAY, 'Pending', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Complete Project Proposal');

INSERT INTO tasks (title, description, due_date, status, created_at, updated_at)
SELECT 'Setup Spring Boot Backend', 'Initialize Spring Boot 3 with JPA and MySQL connection', CURRENT_DATE, 'Completed', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Setup Spring Boot Backend');

INSERT INTO tasks (title, description, due_date, status, created_at, updated_at)
SELECT 'Design React UI Dashboard', 'Create modern responsive dashboard with dark mode and glassmorphism', CURRENT_DATE + INTERVAL 2 DAY, 'Pending', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Design React UI Dashboard');
