package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineTypeDto {
    private Long engineTypeId;
    private String name;
    private String family;
    private String model;
    private String icon;
    private Long categoryId; // only ID to avoid circular references
}
