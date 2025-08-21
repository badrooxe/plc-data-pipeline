package com.plcpipeline.ingestion.services;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.mapper.Mapper;

@Service
public class EngineSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final InfluxDBClient influxDBClient;

    public EngineSseService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void addEmitter(SseEmitter emitter) {
        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out. Removing emitter.");
            this.emitters.remove(emitter);
        });


        emitter.onCompletion(() -> {
            System.out.println("Emitter completed. Removing.");
            this.emitters.remove(emitter);
        });

        emitter.onError(e -> {
            System.out.println("Emitter error: " + e.getMessage());
            this.emitters.remove(emitter);
        });

        this.emitters.add(emitter);
        System.out.println("New SSE client connected. Total clients: " + this.emitters.size());

    }

    public void writeToInfluxAndSendUpdate(Engine engine, TelemetryDataDto telemetry) {
        // Step 1: Write the time-series data to InfluxDB
        writeTelemetryPoint(engine, telemetry);

        // Step 2: Convert the updated Engine entity to a DTO for the SSE payload
        EngineDto updatedEngineDto = Mapper.toEngineDto(engine);

        // Step 3: Broadcast the DTO to all connected clients
        sendEngineUpdate(updatedEngineDto);
    }

    private void writeTelemetryPoint(Engine engine, TelemetryDataDto telemetry) {
        // Implement the logic to write telemetry data to InfluxDB
        Map<String, Object> variables = telemetry.getVariables();
        if (variables == null || variables.isEmpty()) {
            System.out.println("No variables found in telemetry data. Skipping InfluxDB write.");
            return;
        }

        try {
            Point point = Point.measurement("engine_telemetry")
                .setTag("engine_code", engine.getCode())
                .setField("is_active", engine.isActive())
                .setField("hours", engine.getHours())
                .setTimestamp(Instant.parse(telemetry.getTimestamp()));

            influxDBClient.writePoint(point);
        } catch (Exception e) {
            System.out.println("Failed to write telemetry data to InfluxDB: " + e.getMessage());
        }
    }

    public void sendEngineUpdate(EngineDto updatedEngineDto) {
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("engineUpdate")
                    .id(updatedEngineDto.getCode())
                    .data(updatedEngineDto)
                );
            } catch (Exception e) {
                System.out.println("Failed to send update to a client, removing emitter." + e.getMessage());
                this.emitters.remove(emitter);
            }
        }
    }

    

}
