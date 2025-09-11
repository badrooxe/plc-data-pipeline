package com.plcpipeline.ingestion.mtx.client;

import com.plcpipeline.ingestion.mtx.config.MediamtxProperties;
import com.plcpipeline.ingestion.mtx.exception.MediamtxException;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MediamtxClient {
    private static final Logger log = LoggerFactory.getLogger(MediamtxClient.class);

    private final MediamtxProperties props;

    @Qualifier("mediamtxApi")
    private final WebClient mediamtxApi;

    private Duration timeout() {
        return Duration.ofSeconds(props.getRequestTimeoutSeconds());
    }

    public boolean addPath(String pathName, String rtspSource) {
        String url = String.format("/v3/config/paths/add/%s", pathName);

        //String quotedRtspSource = "\"" + rtspSource + "\"";
        String ffmpegCommand = String.format(
    "ffmpeg -rtsp_transport tcp -analyzeduration 3M -probesize 3M -i %s -c copy -f rtsp rtsp://mtxadmin:mtxpass@localhost:8554/%s",
            rtspSource,  // NO quotes - MediaMTX will handle URL parsing
            pathName
        );

        Map<String,Object> body = Map.of(
            "runOnDemand", ffmpegCommand,
            "runOnDemandRestart", true,
            "runOnDemandStartTimeout", "60s",
            "runOnDemandCloseAfter", "60s"
        );

        try {
            var resp = mediamtxApi.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResp -> clientResp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(bodyStr -> Mono.error(new MediamtxException("Add path failed: " + bodyStr, clientResp.rawStatusCode()))))
                    .toBodilessEntity()
                    .timeout(timeout())
                    .block();

            if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
                throw new MediamtxException("Add path failed, status=" + (resp == null ? "null" : resp.getStatusCodeValue()), 502);
            }

            log.info("Added mediamtx path {} -> {}", pathName, rtspSource);
            return true;
        } catch (MediamtxException ex) {
            log.error("Failed to add mediamtx path {}: {}", pathName, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to add mediamtx path {}: {}", pathName, ex.getMessage());
            throw new MediamtxException("Failed to add mediamtx path: " + ex.getMessage(), 502, ex);
        }
    }


    public boolean removePath(String pathName) {
        String url = String.format("/v3/config/paths/delete/%s", pathName);
        try {
            mediamtxApi.delete()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(timeout())
                    .block();
            log.info("Removed mediamtx path {}", pathName);
            return true;
        } catch (Exception ex) {
            log.warn("Failed to remove mediamtx path {}: {}", pathName, ex.getMessage());
            return false;
        }
    }
}
