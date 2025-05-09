package com.example.todo.controller;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTasksByUserId(currentUser.getId()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTasksByStatus(currentUser.getId(), status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(
            @PathVariable String priority,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTasksByPriority(currentUser.getId(), priority));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Task>> getTasksByCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTasksByCategory(currentUser.getId(), categoryId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @Valid @RequestBody Task task,
            @AuthenticationPrincipal User currentUser) {
        task.setUserId(currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Task task = taskService.getById(id);
        if (task == null || !task.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task taskDetails,
            @AuthenticationPrincipal User currentUser) {
        Task task = taskService.getById(id);
        if (task == null || !task.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        taskDetails.setUserId(currentUser.getId()); // 确保userId不被更改
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Task task = taskService.getById(id);
        if (task == null || !task.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}