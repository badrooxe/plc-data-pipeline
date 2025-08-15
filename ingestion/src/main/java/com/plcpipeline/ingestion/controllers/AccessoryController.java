package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.AccessoryDto;
import com.plcpipeline.ingestion.services.AccessoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accessories")
public class AccessoryController {
    private final AccessoryService accessoryService;
    
    public AccessoryController(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @PostMapping
    public ResponseEntity<AccessoryDto> create(@RequestBody AccessoryDto dto) {
        return ResponseEntity.status(201).body(accessoryService.createAccessory(dto));
    }

    @GetMapping
    public ResponseEntity<List<AccessoryDto>> getAll() {
        return ResponseEntity.ok(accessoryService.getAllAccessories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accessoryService.getAccessoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessoryDto> update(@PathVariable Long id, @RequestBody AccessoryDto dto) {
        return ResponseEntity.ok(accessoryService.updateAccessory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accessoryService.deleteAccessory(id);
        return ResponseEntity.noContent().build();
    }
}