package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.AccessoryDto;
import com.plcpipeline.ingestion.services.AccessoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accessories")
public class AccessoryController {
    private final AccessoryService accessoryService;

    public AccessoryController(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @PostMapping
    public AccessoryDto create(@RequestBody AccessoryDto dto) {
        return accessoryService.createAccessory(dto);
    }

    @GetMapping
    public List<AccessoryDto> getAll() {
        return accessoryService.getAllAccessories();
    }

    @GetMapping("/{id}")
    public AccessoryDto getById(@PathVariable Long id) {
        return accessoryService.getAccessoryById(id);
    }

    @PutMapping("/{id}")
    public AccessoryDto update(@PathVariable Long id, @RequestBody AccessoryDto dto) {
        return accessoryService.updateAccessory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accessoryService.deleteAccessory(id);
    }
}