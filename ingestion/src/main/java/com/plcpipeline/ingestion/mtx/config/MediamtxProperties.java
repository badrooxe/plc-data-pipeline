package com.plcpipeline.ingestion.mtx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mediamtx")
public class MediamtxProperties {
    private String host;
    private String readerHost;
    private String apiUser;
    private String apiPass;
    private int pathTtlSeconds = 120;
    private int requestTimeoutSeconds = 5;
}
