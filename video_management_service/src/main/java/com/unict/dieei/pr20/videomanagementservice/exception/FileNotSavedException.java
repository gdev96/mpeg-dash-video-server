package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.HttpStatus;

public class FileNotSavedException extends RestException {

    public FileNotSavedException() {
        super("An error occurred while saving file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
