package com.plcpipeline.ingestion.hik.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "hik")
public class HikCentralProperties {
    private String host;
    //private String apiVer;
    private String appKey;
    private String appSecret;
    private int requestTimeoutSeconds = 10;

    // --- Nested properties for the PREVIEW feature ---
    private final Preview preview = new Preview();

    // --- Nested properties for the PLAYBACK feature ---
    private final Playback playback = new Playback();

    /**
     * Holds all configuration under the 'hik.preview.*' prefix.
     */
    @Data
    public static class Preview {
        private String protocol = "rtsp";
        private int streamType = 0;
        private int transmode = 1;
    }
    
    /**
     * Holds all configuration under the 'hik.playback.*' prefix.
     */
    @Data
    public static class Playback {
        private String protocol = "rtsp";
        private int recordLocation = 1;
        private int startTimeOffsetSeconds = 300;
    }
}
