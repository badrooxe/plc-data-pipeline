package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.TerminalDto;
import com.plcpipeline.ingestion.entities.Port;
import com.plcpipeline.ingestion.entities.Terminal;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.PortRepository;
import com.plcpipeline.ingestion.repositories.TerminalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TerminalService {
    private final TerminalRepository terminalRepository;
    private final PortRepository portRepository;

    public TerminalService(TerminalRepository terminalRepository, PortRepository portRepository) {
        this.terminalRepository = terminalRepository;
        this.portRepository = portRepository;
    }

    public List<TerminalDto> getAllTerminals() {
        return terminalRepository.findAll()
                .stream()
                .map(Mapper::toTerminalDto)
                .collect(Collectors.toList());
    }

    public TerminalDto getTerminalById(Long id) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found with id " + id));
        return Mapper.toTerminalDto(terminal);
    }

    public List<TerminalDto> getTerminalsByPortId(Long portId) {
        return terminalRepository.findByPort_PortId(portId)
                .stream()
                .map(Mapper::toTerminalDto)
                .collect(Collectors.toList());
    }

    public TerminalDto createTerminal(TerminalDto dto) {
        Port port = portRepository.findById(dto.getPortId())
                .orElseThrow(() -> new RuntimeException("Port not found with id " + dto.getPortId()));
        Terminal terminal = Mapper.toTerminalEntity(dto, port);
        terminal = terminalRepository.save(terminal);
        return Mapper.toTerminalDto(terminal);
    }

    public TerminalDto updateTerminal(Long id, TerminalDto dto) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found with id " + id));
        Port port = portRepository.findById(dto.getPortId())
                .orElseThrow(() -> new RuntimeException("Port not found with id " + dto.getPortId()));
        terminal.setName(dto.getName());
        terminal.setLocation(dto.getLocation());
        terminal.setPort(port);
        terminal = terminalRepository.save(terminal);
        return Mapper.toTerminalDto(terminal);
    }

    public void deleteTerminal(Long id) {
        terminalRepository.deleteById(id);
    }
}
