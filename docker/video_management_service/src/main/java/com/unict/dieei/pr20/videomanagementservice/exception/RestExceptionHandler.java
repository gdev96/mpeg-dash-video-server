package com.unict.dieei.pr20.videomanagementservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RestException.class)
    public @ResponseBody ResponseEntity<ApiError> handleException(RestException ex) {
        ZonedDateTime timestamp = ZonedDateTime.now();
        ApiError apiError = new ApiError(timestamp, ex);
        return new ResponseEntity<>(apiError, ex.getStatus());
    }
}
