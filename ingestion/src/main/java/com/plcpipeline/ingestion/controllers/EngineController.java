package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.services.EngineService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/engines")
public class EngineController {
    private final EngineService engineService;

    public EngineController(EngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping
    public ResponseEntity<EngineDto> create(@RequestBody EngineDto dto) {
        return ResponseEntity.status(201).body(engineService.createEngine(dto));
    }

    @GetMapping
    public ResponseEntity<List<EngineDto>> getAll() {
        return ResponseEntity.ok(engineService.getAllEngines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngineDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(engineService.getEngineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EngineDto> update(@PathVariable Long id, @RequestBody EngineDto dto) {
        return ResponseEntity.ok(engineService.updateEngine(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        engineService.deleteEngine(id);
        return ResponseEntity.noContent().build();
    }

    // @GetMapping("/categories")
    // public ResponseEntity<List<String>> getDistinctCategories() {
    //     return ResponseEntity.ok(engineService.getDistinctCategories());
    // }
    
}