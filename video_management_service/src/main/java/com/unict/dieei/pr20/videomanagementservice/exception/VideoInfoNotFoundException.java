package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.HttpStatus;

public class VideoInfoNotFoundException extends RestException {

    public VideoInfoNotFoundException() {
        super("Video info were not found", HttpStatus.BAD_REQUEST);
    }
}
