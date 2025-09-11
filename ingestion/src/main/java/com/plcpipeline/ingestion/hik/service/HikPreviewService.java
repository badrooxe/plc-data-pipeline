package com.plcpipeline.ingestion.hik.service;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.hik.client.HikCentralClient;
import com.plcpipeline.ingestion.hik.dto.PreviewUrlDto;
import com.plcpipeline.ingestion.services.EngineService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HikPreviewService {

    private final EngineService engineService;
    private final HikCentralClient hikClient;

    //@Cacheable(value = "previewUrls", key = "#engineCode", unless = "#result == null || #result.urls.isEmpty()")
    public PreviewUrlDto getPreviewUrlsForEngine(String engineCode) {
        EngineDto engine = engineService.getEngineByCode(engineCode);
        List<String> cameraIds = engineService.getEngineCameraIds(engine);
        if (cameraIds.isEmpty()) {
            return new PreviewUrlDto(engineCode, List.of());
        }
        List<String> urls = hikClient.getPreviewUrlsList(cameraIds);
        return new PreviewUrlDto(engineCode, urls);
    }

    public Mono<ResponseEntity<String>> checkHikCentralHealth() {
        return hikClient.getVersion()
                .map(versionDto -> {
                    // Now we can inspect the structured DTO
                    if ("0".equals(versionDto.getCode())) {
                        String successMsg = String.format(
                            "HikCentral is available.\n Product: %s,\n Version: %s",
                            versionDto.getData().getProduceName(),
                            versionDto.getData().getSoftVersion()
                        );
                        return ResponseEntity.ok(successMsg);
                    } else {
                        // This handles cases where the call succeeded but Hik returned an API error
                        String errorMsg = String.format(
                            "HikCentral is responding, but returned an API error. Code: %s, Message: %s",
                            versionDto.getCode(),
                            versionDto.getMsg()
                        );
                        return ResponseEntity.status(HttpStatus.OK).body(errorMsg);
                    }
                })
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                    .body("HikCentral is unavailable: " + ex.getMessage())
                ));
    }
}
