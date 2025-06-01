package com.RestAPI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.RestAPI.controller.UserController;
import com.RestAPI.entity.User;
import com.RestAPI.exception.UserAlreadyExistsException;
import com.RestAPI.exception.UserNotFoundException;
import com.RestAPI.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class userControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@Test
	void shouldCreateUser() throws Exception {
		User userRequest = new User("John Doe", "johndoe@email.com", "12345678");
		User userResponse = new User(1L, "John Doe", "johndoe@email.com", "12345678");

		when(userService.insertUser(any(User.class))).thenReturn(userResponse);
		
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userRequest)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(userResponse.getId().intValue()))
			.andExpect(jsonPath("$.name").value(userResponse.getName()))
			.andExpect(jsonPath("$.email").value(userResponse.getEmail()))
			.andExpect(jsonPath("$.password").value(userResponse.getPassword()));
	}

	@Test
	void shouldThrowUserAlreadyExistsWhenCreateUserWithSameEmail() {
		when(userService.insertUser(any(User.class))).thenThrow(new UserAlreadyExistsException(null));
	}

	@Test
	void shouldGetUsersEmpty() throws Exception {
		mockMvc.perform(get("/users"))
			.andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}

	@Test
	void shouldGetUsers() throws Exception {
		List<User> users = List.of(
			new User(1L, "John Doe", "johndoe@email.com", "11223344"),
			new User(2L, "Bob", "bob@email.com", "22446688")
		);

		when(userService.getAllUsers()).thenReturn(users);

		mockMvc.perform(get("/users"))
			.andDo(print())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void shouldGetUserById() throws Exception {
		User user = new User(1L, "John Doe", "johndoe@email.com", "11223344");

		when(userService.getUserById(user.getId())).thenReturn(user);

		mockMvc.perform(get("/users/{id}", user.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(user.getName()))
			.andExpect(jsonPath("$.email").value(user.getEmail()));
	}

	@Test
	void shouldThrowUserNotFoundExceptionWhenUserNotFoundById() throws Exception {
		Long userIdNotFound = 1L;

		when(userService.getUserById(userIdNotFound)).thenThrow(new UserNotFoundException("No user found with ID " + userIdNotFound));

		mockMvc.perform(get("/users/{id}", userIdNotFound)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().string("No user found with ID " + userIdNotFound))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldUpdateUserById() throws Exception {
		User updatedUserInfo = new User("John Doe", "johndoe@email.com", "12345678"); 
        User updatedUser = new User(1L, "Bob", "bob@email.com", "12345678");

        when(userService.updateUserById(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)) 
                .andExpect(jsonPath("$.name").value("Bob")) 
                .andExpect(jsonPath("$.email").value("bob@email.com")); 
	}

	@Test
	void shouldDeleteUserById() throws Exception {
		doNothing().when(userService).deleteUserById(1L);

		mockMvc.perform(delete("/users/{id}", 1L))
			.andExpect(status().isOk());
	}
}
