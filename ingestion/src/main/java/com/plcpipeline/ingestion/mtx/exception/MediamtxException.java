package com.plcpipeline.ingestion.mtx.exception;

public class MediamtxException extends RuntimeException {
    private final int status;

    public MediamtxException(String message, int status) {
        super(message);
        this.status = status;
    }
    public MediamtxException(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    public int getStatus() { return status; }
}
