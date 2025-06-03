package com.RestAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RestAPI.dto.TaskDTO;
import com.RestAPI.entity.Task;
import com.RestAPI.exception.TaskNotFoundException;
import com.RestAPI.mapper.TaskDTOMapper;
import com.RestAPI.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDTOMapper taskDTOMapper;

    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(taskDTOMapper)
            .orElseThrow(() -> new TaskNotFoundException("No task found with ID " + id));
    }

    public List<TaskDTO> getTasksUserById(Long id) {
        List<Task> tasks = taskRepository.findAllByAssignedUsers_Id(id);

        if(tasks.isEmpty())
            throw new TaskNotFoundException("No tasks assigned to user with ID " + id);

        return tasks.stream()
            .map(taskDTOMapper)
            .collect(Collectors.toList());
    }

    public TaskDTO createTask(Task task) {
        return taskDTOMapper.apply(taskRepository.save(task));
    }

    public TaskDTO updateTaskById(Long id, Task updatedTask) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                task.setAssignedUsers(updatedTask.getAssignedUsers());

                return taskDTOMapper.apply(taskRepository.save(task));
            })
            .orElseThrow(() -> new TaskNotFoundException("No task found with ID " + id));
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
