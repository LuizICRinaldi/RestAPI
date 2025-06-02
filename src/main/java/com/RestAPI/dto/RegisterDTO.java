package com.RestAPI.dto;

import com.RestAPI.enums.UserRole;

public record RegisterDTO(String name, String email, String password, UserRole role) {}
