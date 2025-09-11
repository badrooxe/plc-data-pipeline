package com.plcpipeline.ingestion.hik.dto;

import lombok.Data;

@Data
public class HikVersionDto {
    private String code;
    private String msg;
    private VersionData data;

    @Data
    public static class VersionData {
        private String produceName;
        private String softVersion;
    }
}