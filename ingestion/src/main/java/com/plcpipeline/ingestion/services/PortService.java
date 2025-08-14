package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.PortDto;
import com.plcpipeline.ingestion.entities.Port;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.PortRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortService {
    private final PortRepository portRepository;

    public PortService(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    public List<PortDto> getAllPorts() {
        return portRepository.findAll()
                .stream()
                .map(Mapper::toPortDto)
                .collect(Collectors.toList());
    }

    public PortDto getPortById(Long id) {
        Port port = portRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Port not found with id " + id));
        return Mapper.toPortDto(port);
    }

    public PortDto createPort(PortDto dto) {
        Port port = Mapper.toPortEntity(dto);
        port = portRepository.save(port);
        return Mapper.toPortDto(port);
    }

    public PortDto updatePort(Long id, PortDto dto) {
        Port port = portRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Port not found with id " + id));
        port.setName(dto.getName());
        port.setDescription(dto.getDescription());
        port.setLocation(dto.getLocation());
        port = portRepository.save(port);
        return Mapper.toPortDto(port);
    }

    public void deletePort(Long id) {
        portRepository.deleteById(id);
    }
}
