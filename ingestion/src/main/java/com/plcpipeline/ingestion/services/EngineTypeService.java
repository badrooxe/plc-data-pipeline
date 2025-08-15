package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.EngineTypeDto;
import com.plcpipeline.ingestion.entities.Category;
import com.plcpipeline.ingestion.entities.EngineType;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.CategoryRepository;
import com.plcpipeline.ingestion.repositories.EngineTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EngineTypeService {

    private final EngineTypeRepository engineTypeRepository;
    private final CategoryRepository categoryRepository;

    public EngineTypeService(EngineTypeRepository engineTypeRepository, CategoryRepository categoryRepository) {
        this.engineTypeRepository = engineTypeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<EngineTypeDto> getAllEngineTypes() {
        return engineTypeRepository.findAll()
                .stream()
                .map(Mapper::toEngineTypeDto)
                .collect(Collectors.toList());
    }

    public EngineTypeDto getEngineTypeById(Long id) {
        EngineType engineType = engineTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EngineType not found with id " + id));
        return Mapper.toEngineTypeDto(engineType);
    }

    public EngineTypeDto createEngineType(EngineTypeDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + dto.getCategoryId()));

        EngineType engineType = Mapper.toEngineTypeEntity(dto);
        engineType.setCategory(category);

        engineType = engineTypeRepository.save(engineType);
        return Mapper.toEngineTypeDto(engineType);
    }

    public EngineTypeDto updateEngineType(Long id, EngineTypeDto dto) {
        EngineType engineType = engineTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EngineType not found with id " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + dto.getCategoryId()));

        engineType.setName(dto.getName());
        engineType.setFamily(dto.getFamily());
        engineType.setModel(dto.getModel());
        engineType.setCategory(category);

        engineType = engineTypeRepository.save(engineType);
        return Mapper.toEngineTypeDto(engineType);
    }

    public void deleteEngineType(Long id) {
        engineTypeRepository.deleteById(id);
    }
}
