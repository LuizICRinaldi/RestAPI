package com.RestAPI.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.RestAPI.dto.UserDTO;
import com.RestAPI.entity.User;
import com.RestAPI.security.SecurityConfigurations;
import com.RestAPI.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "RestAPI", description = "API para um sistema de gestão de tarefas colaborativas, permitindo que usuários criem, editem, atribuam e concluam tarefas.")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Obter informações de todos usuários")
    @ApiResponse(responseCode = "200", description = "Usuário(s) encontrado(s)")
    @ApiResponse(responseCode = "403", description = "Não autorizado")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter informações de um usuário específico")
    @ApiResponse(responseCode = "200", description = "Usuário específico encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário específico não encontrado")
    @ApiResponse(responseCode = "403", description = "Não autorizado")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um novo usuário")
    @ApiResponse(responseCode = "201", description = "Usuário criado")
    @ApiResponse(responseCode = "409", description = "Usuário já existe")
    @ApiResponse(responseCode = "403", description = "Não autorizado")
    public UserDTO createUser(@RequestBody User user) {
        return userService.insertUser(user);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar informações do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "403", description = "Não autorizado")
    public UserDTO updateUserById(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUserById(id, user);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Remover um usuário (soft delete)")
    @ApiResponse(responseCode = "200", description = "Usuário deletado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "403", description = "Não autorizado")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
