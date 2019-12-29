package com.unict.dieei.pr20.videomanagementservice.exception;

public class FileNotSavedException extends RuntimeException {

    public FileNotSavedException() {
        super("An error occurred while saving file");
    }
}
