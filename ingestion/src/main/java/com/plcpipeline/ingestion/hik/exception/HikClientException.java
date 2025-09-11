package com.plcpipeline.ingestion.hik.exception;

public class HikClientException extends RuntimeException {
    private final int status;
    private final String hikCode;

    public HikClientException(String message, int status, String hikCode, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.hikCode = hikCode;
    }

    public HikClientException(String message, int status, String hikCode) {
        super(message);
        this.status = status;
        this.hikCode = hikCode;
    }

    public int getStatus() { return status; }
    public String getHikCode() { return hikCode; }
}
