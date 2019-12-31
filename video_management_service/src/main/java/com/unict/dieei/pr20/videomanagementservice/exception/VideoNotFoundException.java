package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.HttpStatus;

public class VideoNotFoundException extends RestException {

    public VideoNotFoundException() {
        super("The requested video was not found", HttpStatus.NOT_FOUND);
    }
}
