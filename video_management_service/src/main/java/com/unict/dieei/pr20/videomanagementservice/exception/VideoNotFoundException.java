package com.unict.dieei.pr20.videomanagementservice.exception;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException() {
        super("The requested video was not found");
    }
}
