package com.example.todo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.todo.entity.Task;
import com.example.todo.mapper.TaskMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService extends ServiceImpl<TaskMapper, Task> {

    public List<Task> getTasksByUserId(Long userId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    public List<Task> getTasksByStatus(Long userId, String status) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("status", status);
        return list(queryWrapper);
    }

    public List<Task> getTasksByPriority(Long userId, String priority) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("priority", priority);
        return list(queryWrapper);
    }

    public List<Task> getTasksByCategory(Long userId, Long categoryId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("category_id", categoryId);
        return list(queryWrapper);
    }

    public Task createTask(Task task) {
        save(task);
        return task;
    }

    public Task updateTask(Long id, Task task) {
        task.setId(id);
        updateById(task);
        return task;
    }

    public void deleteTask(Long id) {
        removeById(id);
    }
}