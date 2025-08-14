package com.plcpipeline.ingestion.mapper;

import com.plcpipeline.ingestion.entities.*;
import com.plcpipeline.ingestion.dtos.*;

public class Mapper {

    // --- Port ---
    public static PortDto toPortDto(Port port) {
        return PortDto.builder()
                .portId(port.getPortId())
                .name(port.getName())
                .description(port.getDescription())
                .location(port.getLocation())
                .build();
    }

    public static Port toPortEntity(PortDto dto) {
        return Port.builder()
                .portId(dto.getPortId())
                .name(dto.getName())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .build();
    }

    // --- Terminal ---
    public static TerminalDto toTerminalDto(Terminal terminal) {
        return TerminalDto.builder()
                .terminalId(terminal.getTerminalId())
                .name(terminal.getName())
                .location(terminal.getLocation())
                .portId(terminal.getPort().getPortId())
                .build();
    }

    public static Terminal toTerminalEntity(TerminalDto dto, Port port) {
        return Terminal.builder()
                .terminalId(dto.getTerminalId())
                .name(dto.getName())
                .location(dto.getLocation())
                .port(port)
                .build();
    }

    // --- Engine ---
    public static EngineDto toEngineDto(Engine engine) {
        return EngineDto.builder()
                .engineId(engine.getEngineId())
                .code(engine.getCode())
                .name(engine.getName())
                .type(engine.getType())
                .category(engine.getCategory())
                .family(engine.getFamily())
                .ipAddress(engine.getIpAddress())
                .isActive(engine.isActive())
                .lastSeen(engine.getLastSeen())
                .model(engine.getModel())
                .manufacturer(engine.getManufacturer())
                .portId(engine.getPort() != null ? engine.getPort().getPortId() : null)
                .terminalId(engine.getTerminal() != null ? engine.getTerminal().getTerminalId() : null)
                .build();
    }

    public static Engine toEngineEntity(EngineDto dto, Port port, Terminal terminal) {
        return Engine.builder()
                .engineId(dto.getEngineId())
                .code(dto.getCode())
                .name(dto.getName())
                .type(dto.getType())
                .category(dto.getCategory())
                .family(dto.getFamily())
                .ipAddress(dto.getIpAddress())
                .isActive(dto.isActive())
                .lastSeen(dto.getLastSeen())
                .model(dto.getModel())
                .manufacturer(dto.getManufacturer())
                .port(port)
                .terminal(terminal)
                .build();
    }

    // --- Accessory ---
    public static AccessoryDto toAccessoryDto(Accessory accessory) {
        return AccessoryDto.builder()
                .accessoryId(accessory.getAccessoryId())
                .name(accessory.getName())
                .type(accessory.getType())
                .category(accessory.getCategory())
                .build();
    }

    public static Accessory toAccessoryEntity(AccessoryDto dto) {
        return Accessory.builder()
                .accessoryId(dto.getAccessoryId())
                .name(dto.getName())
                .type(dto.getType())
                .category(dto.getCategory())
                .build();
    }
}
