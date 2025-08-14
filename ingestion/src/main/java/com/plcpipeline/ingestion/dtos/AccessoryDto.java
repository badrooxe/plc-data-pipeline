package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccessoryDto {
    private Long accessoryId;
    private String name;
    private String type;
    private String category;
}
