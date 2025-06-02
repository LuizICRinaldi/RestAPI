package com.RestAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.RestAPI.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
    boolean existsByEmail(String email);
}
