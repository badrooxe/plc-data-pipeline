package com.plcpipeline.ingestion.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.plcpipeline.ingestion.dtos.TelemetryDataDto;

@Service
public class TelemetryConsumerService {
    
    @KafkaListener(
        topics = "${spring.kafka.consumer.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "telemetryKafkaListenerFactory"
    )

    public void consume(TelemetryDataDto telemetryData) {
        System.out.println("Received telemetry data: " + telemetryData);
    }
}
