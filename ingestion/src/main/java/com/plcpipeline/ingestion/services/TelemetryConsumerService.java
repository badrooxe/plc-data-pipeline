package com.plcpipeline.ingestion.services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
import com.plcpipeline.ingestion.entities.Engine;

@Service
public class TelemetryConsumerService {

    

    private final EngineService engineService;
    private final EngineSseService engineSseService;
    

    public TelemetryConsumerService(EngineService engineService, EngineSseService engineSseService) {
        this.engineService = engineService;
        this.engineSseService = engineSseService;
    }

    @KafkaListener(
        topics = "${spring.kafka.consumer.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "telemetryKafkaListenerFactory"
        //concurrency = "4"
    )

    @Transactional
    public void consume(
        @Payload TelemetryDataDto telemetryData,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) int offset
    ) {
        if (telemetryData == null || telemetryData.getEngineCode() == null) {
            //System.out.println("Received a null or invalid telemetry message. Discarding.");
            return;
        }
        System.out.println("Received telemetry data from partition " + partition + ", offset " + offset + ": " + telemetryData);

        try{
            // Step 1: Update PostgreSQL
            Engine engine = engineService.findAndUpdateEngineFromTelemetry(telemetryData);

            // Step 2: Write to InfluxDB + push SSE
            engineSseService.writeToInfluxAndSendUpdate(engine, telemetryData);

            System.out.println("Engine processed successfully.");
        } catch (Exception e) {
            System.out.println("Error processing telemetry data '{}': {}" + telemetryData.getEngineCode() + e.getMessage());
        }

    }
}
