package com.taskmanager;

import com.taskmanager.entity.Task;
import com.taskmanager.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class DailyTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyTaskManagerApplication.class, args);
    }

    /**
     * Seeds initial demo tasks if the repository is currently empty
     */
    @Bean
    public CommandLineRunner seedDemoTasks(TaskRepository taskRepository) {
        return args -> {
            if (taskRepository.count() == 0) {
                taskRepository.save(new Task(
                        "Design System & Architecture",
                        "Finalize the database schema and REST API specifications for Daily Task Manager",
                        LocalDate.now().plusDays(1),
                        "Completed"
                ));
                taskRepository.save(new Task(
                        "Review Pull Request #42",
                        "Review backend entity mappings, validation logic, and frontend status toggles",
                        LocalDate.now(),
                        "Pending"
                ));
                taskRepository.save(new Task(
                        "Prepare Team Sprint Review",
                        "Collect task statistics, pending deliverables, and milestones for tomorrow's standup",
                        LocalDate.now().plusDays(2),
                        "Pending"
                ));
            }
        };
    }
}
