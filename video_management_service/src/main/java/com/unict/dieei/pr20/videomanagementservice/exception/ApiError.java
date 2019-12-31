package com.unict.dieei.pr20.videomanagementservice.exception;

import java.time.ZonedDateTime;

public class ApiError {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ApiError(ZonedDateTime timestamp, RestException ex) {
        this.timestamp = timestamp;
        this.status = ex.getStatus().value();
        this.error = ex.getStatus().getReasonPhrase();
        this.message = ex.getMessage();
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
