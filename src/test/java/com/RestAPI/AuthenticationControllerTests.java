package com.RestAPI;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.RestAPI.controller.AuthenticationController;
import com.RestAPI.dto.AuthenticationDTO;
import com.RestAPI.entity.User;
import com.RestAPI.enums.UserRole;
import com.RestAPI.repository.UserRepository;
import com.RestAPI.security.CustomLogoutHandler;
import com.RestAPI.security.SecurityConfigurations;
import com.RestAPI.service.AuthorizationService;
import com.RestAPI.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
@Import({SecurityConfigurations.class})
public class AuthenticationControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private WebApplicationContext context;

    @Autowired
	private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
	private CustomLogoutHandler customLogoutHandler;

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
    void shouldRegisterUser() throws JsonProcessingException, Exception {
        User registerUser = new User("Bob", "bob@email.com", "12345678", UserRole.USER);

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerUser)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldLogin() throws JsonProcessingException, Exception {
        String rawPassword = "12345678";
        User registeredUser = new User(1L, "Bob", "bob@email.com", passwordEncoder.encode(rawPassword), UserRole.USER);
        AuthenticationDTO credentials = new AuthenticationDTO(registeredUser.getEmail(), rawPassword);

        when(authorizationService.loadUserByUsername(credentials.login())).thenReturn(registeredUser);

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(credentials)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldLogout() throws Exception {
        String jwtToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        mockMvc.perform(post("/auth/logout")
            .header(HttpHeaders.AUTHORIZATION, jwtToken))
            .andExpect(status().isOk());
    }
}
