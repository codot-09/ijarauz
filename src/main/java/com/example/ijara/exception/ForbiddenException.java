package com.example.ijara.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Ruxsat etilmagan harakat");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}