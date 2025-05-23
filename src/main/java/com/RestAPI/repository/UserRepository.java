package com.RestAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RestAPI.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
