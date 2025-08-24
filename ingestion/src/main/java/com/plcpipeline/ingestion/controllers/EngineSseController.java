package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.services.EngineSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Engine Real-Time Updates", description = "Endpoints for streaming engine state changes.")
@RestController
@RequestMapping("/engines-sse")
public class EngineSseController {

    @Value("${api.security.key}")
    private String apiKey;

    private final EngineSseService engineSseService;

    public EngineSseController(EngineSseService engineSseService) {
        this.engineSseService = engineSseService;
    }

    @Operation(summary = "Subscribe to real-time engine updates", 
               description = "Opens a Server-Sent Events (SSE) connection. The server will push updates for any engine (e.g., isActive, hours) down this stream.")
    @GetMapping(produces = "text/event-stream")
    public SseEmitter subscribeToEngineUpdates(
        @RequestParam(required = false) List<Long> terminalIds,
        @RequestParam(required = false) List<Long> engineTypeIds,
        @RequestParam(required = true) String sseApiKey
    ) {

        if(!apiKey.equals(sseApiKey)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid SSE API Key");
        }
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        engineSseService.addEmitter(emitter, terminalIds, engineTypeIds);

        engineSseService.sendInitialData(emitter, terminalIds, engineTypeIds);

        return emitter;
    }
}