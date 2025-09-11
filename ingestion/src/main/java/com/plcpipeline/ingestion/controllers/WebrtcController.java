package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.hik.service.HikWebrtcProvisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hik/webrtc")
@RequiredArgsConstructor
public class WebrtcController {

    private final HikWebrtcProvisionService service;

    @PostMapping("/{engineCode}")
    public ResponseEntity<Map<String,String>> provision(@PathVariable String engineCode) {
        String readerUrl = service.provisionForEngine(engineCode);
        return ResponseEntity.ok(Map.of("url", readerUrl));
    }

    @DeleteMapping("/{pathName}")
    public ResponseEntity<?> remove(@PathVariable String pathName) {
        boolean ok = service.unprovision(pathName);
        if (ok) return ResponseEntity.ok().build();
        return ResponseEntity.status(502).body(Map.of("error","failed to remove"));
    }

    @GetMapping("/active")
    public ResponseEntity<?> listActive() {
        return ResponseEntity.ok(service.listActive());
    }
}
