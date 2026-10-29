package com.gym.crm.controller;

import org.springframework.web.bind.annotation.RequestHeader;

public final class AuthHeaders {

    public static final String USERNAME = "X-Auth-Username";
    public static final String PASSWORD = "X-Auth-Password";

    private AuthHeaders() {
    }
}