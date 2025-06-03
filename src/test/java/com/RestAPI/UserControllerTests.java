package com.RestAPI;

import static org.mockito.ArgumentMatchers.any;
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

import com.RestAPI.controller.UserController;
import com.RestAPI.dto.UserDTO;
import com.RestAPI.entity.User;
import com.RestAPI.exception.UserAlreadyExistsException;
import com.RestAPI.exception.UserNotFoundException;
import com.RestAPI.mapper.UserDTOMapper;
import com.RestAPI.repository.UserRepository;
import com.RestAPI.security.SecurityConfigurations;
import com.RestAPI.service.AuthorizationService;
import com.RestAPI.service.TokenService;
import com.RestAPI.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import({SecurityConfigurations.class, UserDTOMapper.class})
class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserDTOMapper userDTOMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private AuthorizationService authorizationService;

	@MockitoBean
	private TokenService tokenService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldCreateUser() throws Exception {
		User userRequest = new User("John Doe", "johndoe@email.com", "12345678");
		UserDTO userResponse = new UserDTO(1L, "John Doe", "johndoe@email.com");

		when(userService.insertUser(any(User.class))).thenReturn(userResponse);
		
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(userResponse)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(userResponse.id().longValue()))
			.andExpect(jsonPath("$.name").value(userRequest.getName()))
			.andExpect(jsonPath("$.email").value(userRequest.getEmail()));
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldThrowUserAlreadyExistsWhenCreateUserWithSameEmail() throws Exception {
		User userRequest = new User("John", "bob@email.com", "12345678");

		when(userService.insertUser(userRequest)).thenThrow(new UserAlreadyExistsException(null));

		mockMvc.perform(post("/users").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldGetUsersEmpty() throws Exception {
		mockMvc.perform(get("/users"))
			.andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldGetUsers() throws Exception {
		List<User> users = List.of(
			new User(1L, "John Doe", "johndoe@email.com", "11223344"),
			new User(2L, "Bob", "bob@email.com", "22446688")
		);

		when(userService.getAllUsers()).thenReturn(users.stream().map(userDTOMapper).collect(Collectors.toList()));

		mockMvc.perform(get("/users"))
			.andDo(print())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldGetUserById() throws Exception {
		User user = new User(1L, "John Doe", "johndoe@email.com", "11223344");

		when(userService.getUserById(user.getId())).thenReturn(userDTOMapper.apply(user));

		mockMvc.perform(get("/users/{id}", user.getId())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(user.getName()))
			.andExpect(jsonPath("$.email").value(user.getEmail()));
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldThrowUserNotFoundExceptionWhenUserNotFoundById() throws Exception {
		Long userIdNotFound = 1L;

		when(userService.getUserById(userIdNotFound)).thenThrow(new UserNotFoundException("No user found with ID " + userIdNotFound));

		mockMvc.perform(get("/users/{id}", userIdNotFound)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().string("No user found with ID " + userIdNotFound))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldUpdateUserById() throws Exception {
		User updatedUserInfo = new User("John Doe", "bob@email.com", "87654321"); 

        when(userService.updateUserById(1L, updatedUserInfo)).thenReturn(userDTOMapper.apply(updatedUserInfo));

        mockMvc.perform(put("/users/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTOMapper.apply(updatedUserInfo))))
                .andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void shouldDeleteUserById() throws Exception {
		doNothing().when(userService).deleteUserById(1L);

		mockMvc.perform(delete("/users/{id}", 1L))
			.andExpect(status().isOk());
	}
}
