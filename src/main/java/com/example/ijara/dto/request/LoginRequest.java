package com.example.ijara.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String authDate;
    private String hash;

    public String getChatId() {
        return id;
    }
}
