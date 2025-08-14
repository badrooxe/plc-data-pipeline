package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.PortDto;
import com.plcpipeline.ingestion.services.PortService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ports")
public class PortController {
    private final PortService portService;

    public PortController(PortService portService) {
        this.portService = portService;
    }

    @GetMapping
    public ResponseEntity<List<PortDto>> getAllPorts() {
        return ResponseEntity.ok(portService.getAllPorts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortDto> getPortById(@PathVariable Long id) {
        return ResponseEntity.ok(portService.getPortById(id));
    }

    @PostMapping
    public ResponseEntity<PortDto> createPort(@RequestBody PortDto dto) {
        PortDto created = portService.createPort(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortDto> updatePort(@PathVariable Long id, @RequestBody PortDto dto) {
        return ResponseEntity.ok(portService.updatePort(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePort(@PathVariable Long id) {
        portService.deletePort(id);
        return ResponseEntity.noContent().build();
    }
}
