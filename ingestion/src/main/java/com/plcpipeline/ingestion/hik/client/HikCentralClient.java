package com.plcpipeline.ingestion.hik.client;

import com.plcpipeline.ingestion.hik.config.HikCentralProperties;
import com.plcpipeline.ingestion.hik.dto.HikPreviewRequestDto;
import com.plcpipeline.ingestion.hik.dto.HikPreviewResponseDto;
import com.plcpipeline.ingestion.hik.dto.HikVersionDto;
import com.plcpipeline.ingestion.hik.exception.HikClientException;
import com.plcpipeline.ingestion.hik.util.SignatureUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HikCentralClient {

    private final HikCentralProperties props;
    private final WebClient hikWebClient; // injected bean (see config below)

    public Mono<HikVersionDto> getVersion() {
        String urlPath = "/artemis/api/common/v1/version";
        
        // Build the signature for this specific request
        String method = "POST";
        String accept = MediaType.APPLICATION_JSON_VALUE;
        // The version endpoint has no body, but the signature often still requires the header
        String contentType = MediaType.APPLICATION_JSON_VALUE; 
        String signature = SignatureUtil.generateSignature(method, accept, contentType, urlPath, props.getAppSecret());

        return hikWebClient.post()
                .uri(urlPath) // Use relative path if WebClient has a baseUrl
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON) // Send the header
                // --- THIS IS THE FIX ---
                .header("X-Ca-Key", props.getAppKey())
                .header("X-Ca-Signature", signature)
                // ---------------------
                .retrieve()
                .bodyToMono(HikVersionDto.class);
    }

    /**
     * Call HikCentral previewURLs endpoint and return raw response DTO
     */
    public HikPreviewResponseDto previewUrls(List<String> myCameraIndexCodes) {
        String urlPath = "/artemis/api/video/v2/cameras/previewURLs";
        String fullUrl = props.getHost() + urlPath;

        HikPreviewRequestDto body = new HikPreviewRequestDto(
                myCameraIndexCodes,
                props.getPreview().getStreamType(),
                props.getPreview().getProtocol(),
                props.getPreview().getTransmode()
        );

        // Object body = new Object() {
        //     public List<String> cameraIndexCodes = myCameraIndexCodes;
        //     public int streamType = props.getPreview().getStreamType();
        //     public String protocol = props.getPreview().getProtocol();
        //     public int transmode = props.getPreview().getTransmode();
        // };

        // Build signature
        String method = "POST";
        String accept = MediaType.APPLICATION_JSON_VALUE;
        String contentType = MediaType.APPLICATION_JSON_VALUE;
        String signature = SignatureUtil.generateSignature(method, accept, contentType, urlPath, props.getAppSecret());

        Long startTime = System.currentTimeMillis();
        // Execute request
        try{
            HikPreviewResponseDto response = hikWebClient.post()
                .uri(fullUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Ca-Key", props.getAppKey())
                .header("X-Ca-Signature", signature)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResp -> 
                    clientResp.bodyToMono(String.class).defaultIfEmpty("").flatMap(bodyStr ->
                        Mono.error(new HikClientException("HikCentral error: " + bodyStr, clientResp.statusCode().value(), null))
                    )
                )
                .bodyToMono(HikPreviewResponseDto.class)
                .timeout(Duration.ofSeconds(props.getRequestTimeoutSeconds()))
                .block(); // <-- blocking here for simplicity (see notes about reactive alternative)

            Long endTime = System.currentTimeMillis();
            System.out.println("HikCentral previewURLs call took " + (endTime - startTime) + " ms");
            System.out.println("urls count: " + (response != null && response.getData() != null && response.getData().getList() != null ? response.getData().getList().size() : 0));
            return response;
        } catch (WebClientResponseException wre) {
            throw new HikClientException("HikCentral HTTP error", wre.getStatusCode().value(), null, wre);
        } catch (Exception ex) {
            throw new HikClientException("HikCentral request failed", 500, null, ex);
        }
        
    }

    /**
     * Convenience: return only the URLs list (flatten)
     */
    public List<String> getPreviewUrlsList(List<String> cameraIndexCodes) {
        HikPreviewResponseDto resp = previewUrls(cameraIndexCodes);
        if (resp == null || !"0".equals(resp.getCode()) || resp.getData() == null || resp.getData().getList() == null) {
            return List.of();
        }
        return resp.getData().getList().stream()
                .map(HikPreviewResponseDto.PreviewItem::getUrl)
                .collect(Collectors.toList());
    }
}
