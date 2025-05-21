package com.RestAPI.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RestAPI.entity.User;
import com.RestAPI.exception.UserAlreadyExistsException;
import com.RestAPI.exception.UserNotFoundException;
import com.RestAPI.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));
    }

    public User insertUser(User user) {
        if(userRepository.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");

        return userRepository.save(user);
    }

    public User updateUserById(Long id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                user.setPassword(updatedUser.getPassword()); 

                return userRepository.save(user);
            })
            .orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
