package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.services.EngineSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Engine Real-Time Updates", description = "Endpoints for streaming engine state changes.")
@RestController
@RequestMapping("/api/v1/engines-sse")
public class EngineSseController {

    private final EngineSseService engineSseService;

    public EngineSseController(EngineSseService engineSseService) {
        this.engineSseService = engineSseService;
    }

    @Operation(summary = "Subscribe to real-time engine updates", 
               description = "Opens a Server-Sent Events (SSE) connection. The server will push updates for any engine (e.g., isActive, hours) down this stream.")
    @GetMapping(produces = "text/event-stream")
    public SseEmitter subscribeToEngineUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        engineSseService.addEmitter(emitter);

        return emitter;
    }
}