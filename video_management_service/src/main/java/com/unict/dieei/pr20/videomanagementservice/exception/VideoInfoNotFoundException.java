package com.unict.dieei.pr20.videomanagementservice.exception;

public class VideoInfoNotFoundException extends RuntimeException {

    public VideoInfoNotFoundException() {
        super("Video info were not found");
    }
}
