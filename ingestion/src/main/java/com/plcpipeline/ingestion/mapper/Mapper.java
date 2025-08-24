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
                //.type(engine.getType())
                //.category(engine.getCategory())
                //.family(engine.getFamily())
                .ipAddress(engine.getIpAddress())
                .isActive(engine.isActive())
                .lastSeen(engine.getLastSeen())
                //.model(engine.getModel())
                //.manufacturer(engine.getManufacturer())
                .hours(engine.getHours())
                .notificationCount(engine.getNotificationCount())
                .portId(engine.getPort() != null ? engine.getPort().getPortId() : null)
                .terminalId(engine.getTerminal() != null ? engine.getTerminal().getTerminalId() : null)
                .engineTypeId(engine.getEngineType() != null ? engine.getEngineType().getEngineTypeId() : null)
                .build();
    }

    public static Engine toEngineEntity(EngineDto dto, Port port, Terminal terminal, EngineType engineType) {
        return Engine.builder()
                .engineId(dto.getEngineId())
                .code(dto.getCode())
                .name(dto.getName())
                //.type(dto.getType())
                //.category(dto.getCategory())
                //.family(dto.getFamily())
                .ipAddress(dto.getIpAddress())
                .isActive(dto.isActive())
                .lastSeen(dto.getLastSeen())
                //.model(dto.getModel())
                //.manufacturer(dto.getManufacturer())
                // this mapper is still fine for api, as it doesn't handle the variables field
                // .hours(dto.getHours())
                // .notificationCount(dto.getNotificationCount())
                .port(port)
                .terminal(terminal)
                .engineType(engineType)
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

    // ====== Category ======
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    public static Category toCategoryEntity(CategoryDto dto) {
        return Category.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .build();
    }

    // ====== EngineType ======
    public static EngineTypeDto toEngineTypeDto(EngineType engineType) {
        return EngineTypeDto.builder()
                .engineTypeId(engineType.getEngineTypeId())
                .name(engineType.getName())
                .family(engineType.getFamily())
                .model(engineType.getModel())
                .icon(engineType.getIcon())
                .categoryId(engineType.getCategory() != null ? engineType.getCategory().getCategoryId() : null)
                .build();
    }

    public static EngineType toEngineTypeEntity(EngineTypeDto dto) {
        return EngineType.builder()
                .engineTypeId(dto.getEngineTypeId())
                .name(dto.getName())
                .family(dto.getFamily())
                .model(dto.getModel())
                .icon(dto.getIcon())
                .category(dto.getCategoryId() != null ? Category.builder().categoryId(dto.getCategoryId()).build() : null)
                .build();
    }
}
