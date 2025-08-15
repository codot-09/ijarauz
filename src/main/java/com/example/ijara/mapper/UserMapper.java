package com.example.ijara.mapper;

import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }
}
