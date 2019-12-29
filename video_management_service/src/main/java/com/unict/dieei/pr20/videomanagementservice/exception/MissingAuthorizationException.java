package com.unict.dieei.pr20.videomanagementservice.exception;

public class MissingAuthorizationException extends RuntimeException {

    public MissingAuthorizationException() {
        super("Authorization was not provided");
    }
}
