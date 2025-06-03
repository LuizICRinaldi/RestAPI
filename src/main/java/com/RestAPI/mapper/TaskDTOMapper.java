package com.RestAPI.mapper;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.RestAPI.dto.TaskDTO;
import com.RestAPI.entity.Task;

@Service
public class TaskDTOMapper implements Function<Task, TaskDTO>{
    private final UserDTOMapper userDTOMapper = new UserDTOMapper();

    @Override
    public TaskDTO apply(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getAssignedUsers()
            .stream()
            .map(userDTOMapper)
            .collect(Collectors.toList()));
    }
}
