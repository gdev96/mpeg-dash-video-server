package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.HttpStatus;

public class UserMismatchException extends RestException {

    public UserMismatchException() {
        super("The requested resource belongs to another user", HttpStatus.FORBIDDEN);
    }
}
