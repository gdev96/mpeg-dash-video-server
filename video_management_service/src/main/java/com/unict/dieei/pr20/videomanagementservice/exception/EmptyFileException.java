package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.HttpStatus;

public class EmptyFileException extends RestException {

    public EmptyFileException() {
        super("Video file is empty", HttpStatus.BAD_REQUEST);
    }
}
