package com.plcpipeline.ingestion.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.repositories.EngineRepository;

@Service
public class TelemetryConsumerService {

    private final EngineService engineService;
    private final EngineRepository engineRepository;
    //private final TelemetryService telemetryService;

    public TelemetryConsumerService(
        EngineService engineService, 
        EngineRepository engineRepository
        ) {
            this.engineService = engineService;
            this.engineRepository = engineRepository;
            //this.telemetryService = telemetryService;
    }

    @KafkaListener(
        topics = "${spring.kafka.consumer.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "telemetryKafkaListenerFactory"
    )

    @Transactional
    public void consume(TelemetryDataDto telemetryData) {
        System.out.println("Received telemetry data: " + telemetryData);

        //Get or create engine
        Engine engine = engineRepository.findByCode(telemetryData.getEngineId())
                .orElseGet(() -> {
                    // Create minimal Engine (you can enhance with more data if available)
                    Engine newEngine = Engine.builder()
                            .code(telemetryData.getEngineId())
                            .isActive(true)
                            .build();
                    return engineRepository.save(newEngine);
                });
        System.out.println("Engine found or created: " + engine);

        //Save telemetry record
        //telemetryService.saveTelemetry(engine, telemetryData);
    }
}
