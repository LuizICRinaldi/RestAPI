package com.RestAPI;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.RestAPI.controller.TaskController;
import com.RestAPI.entity.Task;
import com.RestAPI.entity.User;
import com.RestAPI.exception.TaskNotFoundException;
import com.RestAPI.service.TaskService;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldGetTaskById() throws Exception {
        List<User> assignedUsers = List.of(
            new User(1L, "John Doe", "johndoe@email.com", "12345678"),
            new User(2L, "Bob", "bob@email.com", "12345678")
        );
        Task task = new Task(1L, "Segmentation fault bug", "Some description", assignedUsers);

        when(taskService.getTaskById(anyLong())).thenReturn(task);

        mockMvc.perform(get("/tasks/{id}", anyLong()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenGetTaskById() throws Exception {
        Long id = anyLong();

        when(taskService.getTaskById(id)).thenThrow(new TaskNotFoundException("No task found with ID " + anyLong()));

        mockMvc.perform(get("/tasks/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().string("No task found with ID " + id));
    }

    @Test
    void shouldGetTasksUserById() throws Exception {
        Long id = anyLong();
        User user = new User(id, "Bob", "bob@email.com", "12345678");
        List<Task> tasks = List.of(
            new Task(1L, "Refactor code ", "Refactor service", List.of(user)),
            new Task(2L, "Configure deploy", "Deploy at...", List.of(new User(2L, "Ana", "ana@email.com", "88446622"), user))
        );

        when(taskService.getTasksUserById(id)).thenReturn(tasks);

        mockMvc.perform(get("/tasks?assignedTo={id}", anyLong()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}