package com.unict.dieei.pr20.videomanagementservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User was not found");
    }
}
