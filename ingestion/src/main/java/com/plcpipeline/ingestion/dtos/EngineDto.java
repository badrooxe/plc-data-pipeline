package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EngineDto {
    private Long engineId;
    private String code;
    private String name;
    private String type;
    private String category;
    private String family;
    private String ipAddress;
    private boolean isActive;
    private String lastSeen; // ISO-8601 string
    private String model;
    private String manufacturer;
    private Long portId;
    private Long terminalId;
}