package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.entities.EngineType;
import com.plcpipeline.ingestion.entities.Port;
import com.plcpipeline.ingestion.entities.Terminal;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.EngineRepository;
import com.plcpipeline.ingestion.repositories.EngineTypeRepository;
import com.plcpipeline.ingestion.repositories.PortRepository;
import com.plcpipeline.ingestion.repositories.TerminalRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.plcpipeline.ingestion.exceptions.BadRequestException;
import com.plcpipeline.ingestion.exceptions.ResourceConflictException;
import com.plcpipeline.ingestion.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    //This is the primary method for the Kafka ingestion pipeline.
    // @Transactional
    // public Engine findOrCreateEngineFromTelemetry(TelemetryDataDto telemetryData) {
    //     return engineRepository.findByCode(telemetryData.getEngineCode())
    //             .orElseGet(() -> {

    //                 if (telemetryData.getPortId() == null || telemetryData.getTerminalId() == null || telemetryData.getEngineTypeId() == null) {
    //                     throw new BadRequestException("Cannot create new engine from telemetry. Message for code '" 
    //                     + telemetryData.getEngineCode() + "' is missing required portId, terminalId, or engineTypeId.");
    //                 }

    //                 Port port = portRepository.findById(telemetryData.getPortId()).orElseThrow(() -> new ResourceNotFoundException("Port not found with ID: " + telemetryData.getPortId()));
    //                 Terminal terminal = terminalRepository.findById(telemetryData.getTerminalId()).orElseThrow(() -> new ResourceNotFoundException("Terminal not found with ID: " + telemetryData.getTerminalId()));
    //                 EngineType engineType = engineTypeRepository.findById(telemetryData.getEngineTypeId()).orElseThrow(() -> new ResourceNotFoundException("EngineType not found with ID: " + telemetryData.getEngineTypeId()));

    //                 Engine newEngine = Engine.builder()
    //                         .code(telemetryData.getEngineCode())
    //                         .name(telemetryData.getEngineName())
    //                         .isActive(true)
    //                         .lastSeen(Instant.now().toString())
    //                         .port(port)
    //                         .terminal(terminal)
    //                         .engineType(engineType)
    //                         .build();
    //                 return engineRepository.save(newEngine);
    //             });
    // }

    @Transactional
    public Engine findAndUpdateEngineFromTelemetry(TelemetryDataDto telemetryData) {
        // Find the engine by its unique code, if it does not exist create it.
        Engine engine = engineRepository.findByCode(telemetryData.getEngineCode())
            .orElseGet(() -> {
                if (telemetryData.getPortId() == null || telemetryData.getTerminalId() == null || telemetryData.getEngineTypeId() == null) {
                    throw new BadRequestException("Cannot create new engine from telemetry. Message for code '" 
                    + telemetryData.getEngineCode() + "' is missing required portId, terminalId, or engineTypeId.");
                }

                Port port = portRepository.findById(telemetryData.getPortId())
                        .orElseThrow(() -> new ResourceNotFoundException("Port not found with ID: " + telemetryData.getPortId()));
                Terminal terminal = terminalRepository.findById(telemetryData.getTerminalId())
                        .orElseThrow(() -> new ResourceNotFoundException("Terminal not found with ID: " + telemetryData.getTerminalId()));
                EngineType engineType = engineTypeRepository.findById(telemetryData.getEngineTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("EngineType not found with ID: " + telemetryData.getEngineTypeId()));

                //Engine newEngine = Engine.builder()
                return Engine.builder()
                        .code(telemetryData.getEngineCode())
                        .name(telemetryData.getEngineName())
                        .isActive(true)
                        .lastSeen(Instant.now().toString())
                        .port(port)
                        .terminal(terminal)
                        .engineType(engineType)
                        .build();
                //return engineRepository.save(newEngine);
            });

        // This is where we update the stateful fields in PostgreSQL.(runs for both new and existing engines)
        engine.setLastSeen(telemetryData.getTimestamp() != null ? telemetryData.getTimestamp() : Instant.now().toString());
        engine.setName(telemetryData.getEngineName());
        engine.setIpAddress(telemetryData.getIpAddress());

        Map<String, Object> variables = telemetryData.getVariables();
        if (variables != null) {
            if (variables.containsKey("isActive")) {
                engine.setActive((Boolean) variables.get("isActive"));
            }
            if (variables.containsKey("hours")) {
                engine.setHours(((Number) variables.get("hours")).longValue());
            }
            if (variables.containsKey("notificationCount")) {
                engine.setNotificationCount(((Number) variables.get("notificationCount")).intValue());
            }
        }

        return engineRepository.save(engine);
    }

    public List<EngineDto> getEnginesByTerminalAndEngineType(List<Long> terminalIds, List<Long> engineTypeIds) {
        if (terminalIds == null || terminalIds.isEmpty() || engineTypeIds == null || engineTypeIds.isEmpty()) {
            throw new BadRequestException("Terminal IDs and Engine Type IDs must not be null or empty.");
        }
        List<Engine> engines = engineRepository.findByTerminalIdsAndEngineTypeIds(terminalIds, engineTypeIds);
        if (engines.isEmpty()) {
            throw new ResourceNotFoundException("No engines found for the provided terminal and engine type IDs.");
        }
        return engines.stream()
                .map(Mapper::toEngineDto)
                .collect(Collectors.toList());
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
       // existing.setManufacturer(dto.getManufacturer());
        return Mapper.toEngineDto(engineRepository.save(existing));
    }

    public void deleteEngine(Long id) {
        engineRepository.deleteById(id);
    }
}