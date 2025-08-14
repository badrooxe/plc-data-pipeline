package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortDto {
    private Long portId;
    private String name;
    private String description;
    private String location;
}
