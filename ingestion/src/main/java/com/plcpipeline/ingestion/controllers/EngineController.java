package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.services.EngineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/engines")
public class EngineController {
    private final EngineService engineService;

    public EngineController(EngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping
    public EngineDto create(@RequestBody EngineDto dto) {
        return engineService.createEngine(dto);
    }

    @GetMapping
    public List<EngineDto> getAll() {
        return engineService.getAllEngines();
    }

    @GetMapping("/{id}")
    public EngineDto getById(@PathVariable Long id) {
        return engineService.getEngineById(id);
    }

    @PutMapping("/{id}")
    public EngineDto update(@PathVariable Long id, @RequestBody EngineDto dto) {
        return engineService.updateEngine(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        engineService.deleteEngine(id);
    }
}