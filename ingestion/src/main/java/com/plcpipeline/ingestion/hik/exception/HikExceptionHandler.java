package com.plcpipeline.ingestion.hik.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class HikExceptionHandler {

    @ExceptionHandler(HikClientException.class)
    public ResponseEntity<Object> handleHikClientException(HikClientException ex) {
        // Map<String, Object> body = new HashMap<>();
        // body.put("timestamp", LocalDateTime.now());
        // body.put("status", HttpStatus.NOT_FOUND.value());
        // body.put("error", "Not Found");
        // body.put("message", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        if (ex.getStatus() == 429) status = HttpStatus.TOO_MANY_REQUESTS;
        else if (ex.getStatus() >= 500 && ex.getStatus() < 600) status = HttpStatus.BAD_GATEWAY;
        else if (ex.getStatus() >= 400 && ex.getStatus() < 500) status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), ex.getMessage(), ex.getHikCode()));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, ex.getMessage(), null));
    }

    // Generic
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        System.out.println("Unhandled exception:" + ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(500, "Internal error", null));
    }

    record ErrorResponse(int status, String message, String upstreamCode) {}

}