package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.EngineTypeDto;
import com.plcpipeline.ingestion.services.EngineTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/engine-types")
public class EngineTypeController {

    private final EngineTypeService engineTypeService;
    public EngineTypeController(EngineTypeService engineTypeService) {
        this.engineTypeService = engineTypeService;
    }

    @PostMapping
    public ResponseEntity<EngineTypeDto> create(@RequestBody EngineTypeDto dto) {
        return ResponseEntity.status(201).body(engineTypeService.createEngineType(dto));
    }

    @GetMapping
    public ResponseEntity<List<EngineTypeDto>> getAll() {
        return ResponseEntity.ok(engineTypeService.getAllEngineTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngineTypeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(engineTypeService.getEngineTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EngineTypeDto> update(@PathVariable Long id, @RequestBody EngineTypeDto dto) {
        return ResponseEntity.ok(engineTypeService.updateEngineType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        engineTypeService.deleteEngineType(id);
        return ResponseEntity.noContent().build();
    }
}
