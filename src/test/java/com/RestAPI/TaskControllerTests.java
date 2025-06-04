package com.RestAPI;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.RestAPI.controller.TaskController;
import com.RestAPI.dto.TaskDTO;
import com.RestAPI.entity.Task;
import com.RestAPI.entity.User;
import com.RestAPI.enums.Status;
import com.RestAPI.exception.TaskNotFoundException;
import com.RestAPI.mapper.TaskDTOMapper;
import com.RestAPI.repository.UserRepository;
import com.RestAPI.security.CustomLogoutHandler;
import com.RestAPI.security.SecurityConfigurations;
import com.RestAPI.service.AuthorizationService;
import com.RestAPI.service.TaskService;
import com.RestAPI.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
@Import({SecurityConfigurations.class, TaskDTOMapper.class})
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private WebApplicationContext context;

    @Autowired
    private TaskDTOMapper taskDTOMapper;

    @Autowired
	private ObjectMapper objectMapper;

    @MockitoBean
	private UserRepository userRepository;

    @MockitoBean
	private CustomLogoutHandler customLogoutHandler;

    @MockitoBean
	private AuthorizationService authorizationService;

	@MockitoBean
	private TokenService tokenService;

    @MockitoBean
    private TaskService taskService;

    @BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldGetTaskById() throws Exception {
        List<User> assignedUsers = List.of(
            new User(1L, "John Doe", "johndoe@email.com", "12345678"),
            new User(2L, "Bob", "bob@email.com", "12345678")
        );
        Task task = new Task(1L, "Segmentation fault bug", "Some description", assignedUsers);

        when(taskService.getTaskById(anyLong())).thenReturn(taskDTOMapper.apply(task));

        mockMvc.perform(get("/tasks/{id}", anyLong()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldThrowTaskNotFoundExceptionWhenGetTaskById() throws Exception {
        Long id = anyLong();

        when(taskService.getTaskById(id)).thenThrow(new TaskNotFoundException("No task found with ID " + anyLong()));

        mockMvc.perform(get("/tasks/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().string("No task found with ID " + id));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldGetTasksUserById() throws Exception {
        Long id = anyLong();
        User user = new User(id, "Bob", "bob@email.com", "12345678");
        List<Task> tasks = List.of(
            new Task(1L, "Refactor code ", "Refactor service", List.of(user)),
            new Task(2L, "Configure deploy", "Deploy at...", List.of(new User(2L, "Ana", "ana@email.com", "88446622"), user))
        );

        when(taskService.getTasksUserById(id)).thenReturn(tasks.stream()
            .map(taskDTOMapper).collect(Collectors.toList()));

        mockMvc.perform(get("/tasks?assignedTo={id}", anyLong()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldCreateTask() throws Exception {
        User assignedUser = new User(1L, "Bob", "bob@email.com");
        Task taskToBeCreated = new Task("Task title", "Some description...", List.of(assignedUser));
        TaskDTO taskToBeCreatedDTO = taskDTOMapper.apply(taskToBeCreated);

        when(taskService.createTask(taskToBeCreated)).thenReturn(taskDTOMapper.apply(taskToBeCreated));

        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(taskToBeCreatedDTO)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldUpdateTaskById() throws Exception {
        User assignedUser = new User(1L, "Bob", "bob@email.com");
        Task task = new Task(1L, "Title", "Description", List.of(assignedUser));
        Task updatedTask = task;
        updatedTask.setStatus(Status.DONE);

        when(taskService.updateTaskById(1L, updatedTask)).thenReturn(taskDTOMapper.apply(updatedTask));

        mockMvc.perform(put("/tasks/{id}", task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(taskDTOMapper.apply(updatedTask))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldDeleteTaskById() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", anyLong()))
            .andExpect(status().isOk());
    }
}