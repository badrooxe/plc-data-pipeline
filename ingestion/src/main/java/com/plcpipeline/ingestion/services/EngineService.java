package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.entities.EngineType;
import com.plcpipeline.ingestion.entities.Port;
import com.plcpipeline.ingestion.entities.Terminal;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.EngineRepository;
import com.plcpipeline.ingestion.repositories.EngineTypeRepository;
import com.plcpipeline.ingestion.repositories.PortRepository;
import com.plcpipeline.ingestion.repositories.TerminalRepository;

import org.springframework.stereotype.Service;

import com.plcpipeline.ingestion.exceptions.ResourceConflictException;
import com.plcpipeline.ingestion.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EngineService {
    private final EngineRepository engineRepository;
    private final PortRepository portRepository;
    private final TerminalRepository terminalRepository;
    private final EngineTypeRepository engineTypeRepository;

    public EngineService(EngineRepository engineRepository, PortRepository portRepository, TerminalRepository terminalRepository, EngineTypeRepository engineTypeRepository) {
        this.engineRepository = engineRepository;
        this.portRepository = portRepository;
        this.terminalRepository = terminalRepository;
        this.engineTypeRepository = engineTypeRepository;
    }

    public EngineDto createEngine(EngineDto dto) {
        
        if(engineRepository.findByCode(dto.getCode()).isPresent()) {
            throw new ResourceConflictException("Engine with code " + dto.getCode() + " already exists.");
        }
        
        Port port = portRepository.findById(dto.getPortId())
                .orElseThrow(() -> new ResourceNotFoundException("Port not found with ID: " + dto.getPortId()));

        Terminal terminal = terminalRepository.findById(dto.getTerminalId())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found with ID: " + dto.getTerminalId()));

        EngineType engineType = engineTypeRepository.findById(dto.getEngineTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("EngineType not found with ID: " + dto.getEngineTypeId()));

        Engine engine = Mapper.toEngineEntity(dto, port, terminal, engineType);
        return Mapper.toEngineDto(engineRepository.save(engine));
    }

    // Retrieves an engine by its code, or creates a new one if it doesn't exist
    public Engine getOrCreateByCode(String code, String name, Long portId, Long terminalId, Long engineTypeId) {
        return engineRepository.findByCode(code).orElseGet(() -> {
            Port port = (portId != null) ? portRepository.findById(portId).orElseThrow(() -> new ResourceNotFoundException("Port not found with ID: " + portId)) : null;
            Terminal terminal = (terminalId != null) ? terminalRepository.findById(terminalId).orElseThrow(() -> new ResourceNotFoundException("Terminal not found with ID: " + terminalId)) : null;
            EngineType engineType = (engineTypeId != null) ? engineTypeRepository.findById(engineTypeId).orElseThrow(() -> new ResourceNotFoundException("EngineType not found with ID: " + engineTypeId)) : null;

            Engine newEngine = Engine.builder()
                    .code(code)
                    .name(name != null ? name : "Unnamed Engine")
                    .isActive(true)
                    .lastSeen(Instant.now().toString())
                    .port(port)
                    .terminal(terminal)
                    .engineType(engineType)
                    .build();
            return engineRepository.save(newEngine);
        });
    }

    // public List<String> getDistinctCategories() {
    //     return engineRepository.findDistinctCategories();
    // }

    public List<EngineDto> getAllEngines() {
        return engineRepository.findAll().stream().map(Mapper::toEngineDto).collect(Collectors.toList());
    }

    public EngineDto getEngineById(Long id) {
        return engineRepository.findById(id).map(Mapper::toEngineDto).orElse(null);
    }

    public EngineDto updateEngine(Long id, EngineDto dto) {
        Engine existing = engineRepository.findById(id).orElseThrow();
        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        //existing.setType(dto.getType());
        //existing.setCategory(dto.getCategory());
        //existing.setFamily(dto.getFamily());
        existing.setIpAddress(dto.getIpAddress());
        existing.setActive(dto.isActive());
        existing.setLastSeen(dto.getLastSeen());
        //existing.setModel(dto.getModel());
        existing.setManufacturer(dto.getManufacturer());
        return Mapper.toEngineDto(engineRepository.save(existing));
    }

    public void deleteEngine(Long id) {
        engineRepository.deleteById(id);
    }
}