package com.example.ijara.dto.response;

import com.example.ijara.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String accessToken;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String username;
}