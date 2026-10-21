package com.gym.crm.service.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("Invalid username or password.");
    }
}