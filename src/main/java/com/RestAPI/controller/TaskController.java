package com.RestAPI.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RestAPI.entity.Task;
import com.RestAPI.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de uma tarefa")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    @Operation(summary = "Listar todas as tarefas atribuídas a um usuário")
    @ApiResponse(responseCode = "200", description = "Tarefas atribuídas a um usuário encontradas")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public List<Task> getTasksUserById(@RequestParam("assignedTo") Long id) {
        return taskService.getTasksUserById(id);
    }

    @PostMapping
    @Operation(summary = "Criar uma nova tarefa")
    @ApiResponse(responseCode = "200", description = "Tarefa criada")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar informações da tarefa (título, descrição, status)")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public Task updateTaskById(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTaskById(id, task);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Remover uma tarefa")
    @ApiResponse(responseCode = "200", description = "Tarefa deletada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public void deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}
