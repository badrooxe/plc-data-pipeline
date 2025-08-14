package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.AccessoryDto;
import com.plcpipeline.ingestion.entities.Accessory;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.AccessoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessoryService {
    private final AccessoryRepository accessoryRepository;

    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    public AccessoryDto createAccessory(AccessoryDto dto) {
        Accessory accessory = Mapper.toAccessoryEntity(dto);
        return Mapper.toAccessoryDto(accessoryRepository.save(accessory));
    }

    public List<AccessoryDto> getAllAccessories() {
        return accessoryRepository.findAll().stream().map(Mapper::toAccessoryDto).collect(Collectors.toList());
    }

    public AccessoryDto getAccessoryById(Long id) {
        return accessoryRepository.findById(id).map(Mapper::toAccessoryDto).orElse(null);
    }

    public AccessoryDto updateAccessory(Long id, AccessoryDto dto) {
        Accessory existing = accessoryRepository.findById(id).orElseThrow();
        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setCategory(dto.getCategory());
        return Mapper.toAccessoryDto(accessoryRepository.save(existing));
    }

    public void deleteAccessory(Long id) {
        accessoryRepository.deleteById(id);
    }
}