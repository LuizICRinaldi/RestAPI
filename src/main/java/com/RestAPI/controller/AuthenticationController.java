package com.RestAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RestAPI.dto.AuthenticationDTO;
import com.RestAPI.dto.LoginResponseDTO;
import com.RestAPI.dto.RegisterDTO;
import com.RestAPI.entity.User;
import com.RestAPI.repository.UserRepository;
import com.RestAPI.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    
    @PostMapping("/login")
    @Operation(summary = "Login de usuário")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "403", description = "Proibido")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        String token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário")
    @ApiResponse(responseCode = "200", description = "Usuário registrado")
    @ApiResponse(responseCode = "403", description = "Usuário já existe")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterDTO data) {
        if(userRepository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().build();
        
        String encriptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encriptedPassword, data.role());
        userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout do usuário")
    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    public String performLogout(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, auth);
        return "You have successfully logged out";
    }
}
