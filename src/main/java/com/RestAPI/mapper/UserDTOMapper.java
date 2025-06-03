package com.RestAPI.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.RestAPI.dto.UserDTO;
import com.RestAPI.entity.User;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
