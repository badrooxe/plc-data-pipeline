package com.plcpipeline.ingestion.services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plcpipeline.ingestion.dtos.TelemetryDataDto;

@Service
public class TelemetryConsumerService {

    

    private final EngineService engineService;

    public TelemetryConsumerService(EngineService engineService) {
        this.engineService = engineService;
    }

    @KafkaListener(
        topics = "${spring.kafka.consumer.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "telemetryKafkaListenerFactory"
    )

    @Transactional
    public void consume(TelemetryDataDto telemetryData) {
        if (telemetryData == null || telemetryData.getEngineCode() == null) {
            System.out.println("Received a null or invalid telemetry message. Discarding.");
            return;
        }
        System.out.println("Received telemetry data: " + telemetryData);

        try{
            engineService.findOrCreateEngineFromTelemetry(telemetryData);
            System.out.println("Engine processed successfully.");
        } catch (Exception e) {
            System.out.println("Error processing telemetry data '{}': {}" + telemetryData.getEngineCode() + e.getMessage());
        }


        //Get or create engine
        // Engine engine = engineRepository.findByCode(telemetryData.getEngineCode())
        //         .orElseGet(() -> {
        //             // Create minimal Engine (you can enhance with more data if available)
        //             Engine newEngine = Engine.builder()
        //                     .code(telemetryData.getEngineCode())
        //                     .name(telemetryData.getEngineName())
        //                     .ipAddress(telemetryData.getIpAddress())
        //                     .port(telemetryData.getPortId() != null ? telemetryData.getPortId() : null)
        //                     .terminal(telemetryData.getTerminalId() != null ? telemetryData.getTerminalId() : null)
        //                     .engineType(telemetryData.getEngineTypeId() != null ? telemetryData.getEngineTypeId() : null)
        //                     .lastSeen(telemetryData.getTimestamp() != null ? telemetryData.getTimestamp() : Instant.now().toString())
        //                     .isActive(true)
        //                     .build();
        //             return engineRepository.save(newEngine);
        //         });
        // System.out.println("Engine found or created: " + engine);

        //Save telemetry record
        //telemetryService.saveTelemetry(engine, telemetryData);
    }
}
