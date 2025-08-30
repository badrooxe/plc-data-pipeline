package com.plcpipeline.ingestion.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationDto {
    private Long id;
    private String severity;
    private String message;
    private Long timestamp;
    private int triggerValue;
    private Long engineId; // just the id, not full Engine object
}
