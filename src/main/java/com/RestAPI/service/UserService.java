package com.RestAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RestAPI.dto.UserDTO;
import com.RestAPI.entity.User;
import com.RestAPI.exception.UserAlreadyExistsException;
import com.RestAPI.exception.UserNotFoundException;
import com.RestAPI.mapper.UserDTOMapper;
import com.RestAPI.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOMapper userDTOMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(userDTOMapper)
            .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
            .map(userDTOMapper)
            .orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));
    }

    public UserDTO insertUser(User user) {
        if(userRepository.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");

        return userDTOMapper.apply(userRepository.save(user));
    }

    public UserDTO updateUserById(Long id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                user.setPassword(updatedUser.getPassword()); 

                return userDTOMapper.apply(userRepository.save(user));
            })
            .orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));
    }

    public void deleteUserById(Long id) {
        if(!userRepository.existsById(id))
            throw new UserNotFoundException("No user found with ID " + id);
        
        userRepository.deleteById(id);
    }
}
