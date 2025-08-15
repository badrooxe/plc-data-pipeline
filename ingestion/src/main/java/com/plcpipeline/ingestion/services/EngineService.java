package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.entities.Port;
import com.plcpipeline.ingestion.entities.Terminal;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.EngineRepository;
import com.plcpipeline.ingestion.repositories.PortRepository;
import com.plcpipeline.ingestion.repositories.TerminalRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EngineService {
    private final EngineRepository engineRepository;
    private final PortRepository portRepository;
    private final TerminalRepository terminalRepository;

    public EngineService(EngineRepository engineRepository, PortRepository portRepository, TerminalRepository terminalRepository) {
        this.engineRepository = engineRepository;
        this.portRepository = portRepository;
        this.terminalRepository = terminalRepository;
    }

    public EngineDto createEngine(EngineDto dto) {
        Port port = portRepository.findById(dto.getPortId()).orElse(null);
        Terminal terminal = terminalRepository.findById(dto.getTerminalId()).orElse(null);
        Engine engine = Mapper.toEngineEntity(dto, port, terminal);
        return Mapper.toEngineDto(engineRepository.save(engine));
    }

    // Retrieves an engine by its code, or creates a new one if it doesn't exist
    public Engine getOrCreateByCode(String code, String name, Long portId, Long terminalId) {
        return engineRepository.findByCode(code).orElseGet(() -> {
            Port port = (portId != null) ? portRepository.findById(portId).orElse(null) : null;
            Terminal terminal = (terminalId != null) ? terminalRepository.findById(terminalId).orElse(null) : null;

            Engine newEngine = Engine.builder()
                    .code(code)
                    .name(name != null ? name : "Unnamed Engine")
                    .isActive(true)
                    .lastSeen(Instant.now().toString())
                    .port(port)
                    .terminal(terminal)
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
        existing.setType(dto.getType());
        existing.setCategory(dto.getCategory());
        existing.setFamily(dto.getFamily());
        existing.setIpAddress(dto.getIpAddress());
        existing.setActive(dto.isActive());
        existing.setLastSeen(dto.getLastSeen());
        existing.setModel(dto.getModel());
        existing.setManufacturer(dto.getManufacturer());
        return Mapper.toEngineDto(engineRepository.save(existing));
    }

    public void deleteEngine(Long id) {
        engineRepository.deleteById(id);
    }
}