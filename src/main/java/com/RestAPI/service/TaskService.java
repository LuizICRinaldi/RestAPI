package com.RestAPI.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RestAPI.entity.Task;
import com.RestAPI.exception.TaskNotFoundException;
import com.RestAPI.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("No task found with ID " + id));
    }

    public List<Task> getTasksUserById(Long id) {
        List<Task> tasks = taskRepository.findAllByAssignedUsers_Id(id);

        if(tasks.isEmpty())
            throw new TaskNotFoundException("No tasks assigned to user with ID " + id);

        return tasks;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTaskById(Long id, Task updatedTask) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                task.setAssignedUsers(updatedTask.getAssignedUsers());

                return taskRepository.save(task);
            })
            .orElseThrow(() -> new TaskNotFoundException("No task found with ID " + id));
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
