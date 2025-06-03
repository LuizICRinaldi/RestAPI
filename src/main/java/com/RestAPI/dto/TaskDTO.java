package com.RestAPI.dto;

import java.util.List;

import com.RestAPI.enums.Status;

public record TaskDTO(Long id, String title, String description, Status status, List<UserDTO> assignedUsers) {}
