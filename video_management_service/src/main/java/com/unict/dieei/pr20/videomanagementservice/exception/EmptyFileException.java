package com.unict.dieei.pr20.videomanagementservice.exception;

public class EmptyFileException extends RuntimeException {

    public EmptyFileException() {
        super("Video file is empty");
    }
}
