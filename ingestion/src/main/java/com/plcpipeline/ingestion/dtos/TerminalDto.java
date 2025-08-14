package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminalDto {
    private Long terminalId;
    private String name;
    private String location;
    private Long portId;  // Just ID to avoid circular ref
}
