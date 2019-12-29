package com.unict.dieei.pr20.videomanagementservice.exception;

public class UserMismatchException extends RuntimeException {

    public UserMismatchException() {
        super("The requested resource belongs to another user");
    }
}
