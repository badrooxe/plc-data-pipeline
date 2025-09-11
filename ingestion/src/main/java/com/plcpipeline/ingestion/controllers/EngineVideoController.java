package com.plcpipeline.ingestion.controllers;

//import com.plcpipeline.ingestion.hik.config.HikCentralProperties;
import com.plcpipeline.ingestion.hik.dto.PreviewUrlDto;
import com.plcpipeline.ingestion.hik.service.HikPreviewService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;


@RestController
@RequestMapping("/api/v1/hik")
@RequiredArgsConstructor
public class EngineVideoController {

    private final HikPreviewService hikPreviewService;
    //private final WebClient hikWebClient; // hik-specific bean
    //private final HikCentralProperties props;

    @GetMapping("/previewUrls/{engineCode}")
    public ResponseEntity<PreviewUrlDto> getPreviewUrls(
            @PathVariable String engineCode
    ) {
        PreviewUrlDto dto = hikPreviewService.getPreviewUrlsForEngine(engineCode);
        return ResponseEntity.ok(dto);
    }

    // for testing connectivity to HikCentral
    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        // String urlPath = "/artemis/api/common/" + props.getApiVer() + "/version";
        // String full = props.getHost() + urlPath;

        // return hikWebClient.post()
        //         .uri(full)
        //         .accept(org.springframework.http.MediaType.APPLICATION_JSON)
        //         .retrieve()
        //         .bodyToMono(String.class)
        //         .map(body -> ResponseEntity.ok("OK"))
        //         .onErrorResume(ex -> Mono.just(ResponseEntity.status(502).body("Hik unavailable: " + ex.getMessage())));
        return hikPreviewService.checkHikCentralHealth();
    }
}
