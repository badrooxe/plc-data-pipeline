package com.plcpipeline.ingestion.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.plcpipeline.ingestion.dtos.EngineDto;
import com.plcpipeline.ingestion.dtos.TelemetryDataDto;
import com.plcpipeline.ingestion.entities.Engine;


@Service
public class EngineSseService {

    private static class Client {
    SseEmitter emitter;
    List<Long> terminalIds;
    List<Long> engineTypeIds;

    Client(SseEmitter emitter, List<Long> terminalIds, List<Long> engineTypeIds) {
        this.emitter = emitter;
        this.terminalIds = terminalIds;
        this.engineTypeIds = engineTypeIds;
    }
}

    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final InfluxDBClient influxDBClient;

    public EngineSseService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void addEmitter(SseEmitter emitter, List<Long> terminalIds, List<Long> engineTypeIds) {
        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out. Removing emitter.");
            this.clients.removeIf(c -> c.emitter == emitter);
        });


        emitter.onCompletion(() -> {
            System.out.println("Emitter completed. Removing.");
            this.clients.removeIf(c -> c.emitter == emitter);
        });

        emitter.onError(e -> {
            System.out.println("Emitter error: " + e.getMessage());
            this.clients.removeIf(c -> c.emitter == emitter);
        });

        this.clients.add(new Client(emitter, terminalIds, engineTypeIds));
        System.out.println("New SSE client connected. Total clients: " + this.clients.size());

    }

    public void writeToInfluxAndSendUpdate(Engine engine, TelemetryDataDto telemetry) {
        // Step 1: Write the time-series data to InfluxDB
        writeTelemetryPoint(engine, telemetry);

        // Step 2: Convert the updated Engine entity to a DTO for the SSE payload
        //EngineDto updatedEngineDto = Mapper.toEngineDto(engine);
        List<EngineDto> latestStates = getLatestStateForAllEngines();

        // Step 3: Broadcast the DTO to all connected clients
        //sendEngineUpdate(updatedEngineDto);
        sendEngineUpdate(latestStates);
    }

    private void writeTelemetryPoint(Engine engine, TelemetryDataDto telemetry) {
        // Implement the logic to write telemetry data to InfluxDB
        Map<String, Object> variables = telemetry.getVariables();
        if (variables == null || variables.isEmpty()) {
            System.out.println("No variables found in telemetry data. Skipping InfluxDB write.");
            return;
        }

        try {
            Point point = Point.measurement("engine_telemetry2")
                .setField("engineId", engine.getEngineId())
                .setField("engineCode", telemetry.getEngineCode())
                .setField("engineName", telemetry.getEngineName())
                .setField("ipAddress", telemetry.getIpAddress())
                .setField("lastSeen", telemetry.getTimestamp() != null ? telemetry.getTimestamp() : Instant.now().toString())
                .setField("portId", telemetry.getPortId())
                .setField("terminalId", telemetry.getTerminalId())
                .setField("engineTypeId", telemetry.getEngineTypeId())
                .setField("isActive", telemetry.getVariables().get("isActive"))
                .setField("hours", telemetry.getVariables().get("hours"))
                .setField("notificationCount", telemetry.getVariables().get("notificationCount"))
                .setTimestamp(Instant.parse(telemetry.getTimestamp()));

            influxDBClient.writePoint(point);
        } catch (Exception e) {
            System.out.println("Failed to write telemetry data to InfluxDB: " + e.getMessage());
        }
    }

    public List<EngineDto> getLatestStateForAllEngines() {
        //List<Map<String, Object>> latestStates = new ArrayList<>();
        List<EngineDto> latestStates = new ArrayList<>();
        String sql = "SELECT t.\"engineId\", t.\"engineCode\", t.\"engineName\", t.\"ipAddress\", t.\"lastSeen\", t.\"portId\", t.\"terminalId\", t.\"engineTypeId\", t.\"isActive\", t.\"hours\", t.\"notificationCount\" FROM engine_telemetry2 t " +
                     "INNER JOIN (" +
                        " SELECT \"engineCode\", MAX(time) AS max_time " +
                        "  FROM engine_telemetry2 " +
                        "  GROUP BY \"engineCode\"" +
                     ") latest ON t.\"engineCode\" = latest.\"engineCode\" AND t.time = latest.max_time";

        try(Stream<Object[]> stream = influxDBClient.query(sql)){
            stream.forEach(row -> {
                EngineDto engineDto = new EngineDto();
                System.out.println("Processing row: " + Arrays.toString(row));
                engineDto.setEngineId(((Number) row[0]).longValue());
                engineDto.setCode((String) row[1]);
                engineDto.setName((String) row[2]);
                engineDto.setIpAddress((String) row[3]);
                engineDto.setLastSeen((String) row[4]);
                engineDto.setPortId(((Number) row[5]).longValue());
                engineDto.setTerminalId(((Number) row[6]).longValue());
                engineDto.setEngineTypeId(((Number) row[7]).longValue());
                engineDto.setActive((Boolean) row[8]);
                engineDto.setHours(((Number) row[9]).longValue());
                engineDto.setNotificationCount(((Number) row[10]).intValue());
                latestStates.add(engineDto);
            });
        }
        return latestStates;
    }

    public void sendEngineUpdate(List<EngineDto> updatedEngineDtos) {
        for (Client client : clients) {
            try {
                // Filter engines based on client subscriptions
                List<EngineDto> filtered = updatedEngineDtos.stream()
                    .filter(e -> (client.terminalIds == null || client.terminalIds.contains(e.getTerminalId())) &&
                                (client.engineTypeIds == null || client.engineTypeIds.contains(e.getEngineTypeId())))
                    .toList();

                if (!filtered.isEmpty()) {
                    client.emitter.send(SseEmitter.event()
                        .name("engineUpdate")
                        .data(Map.of("engines", filtered))
                    );
                }
            } catch (Exception e) {
                System.out.println("Failed to send updates, removing emitter: " + e.getMessage());
                clients.remove(client);
            }
        }
    }


    public void sendInitialData(SseEmitter emitter, List<Long> terminalIds, List<Long> engineTypeIds) {
        List<EngineDto> snapshot = getLatestStateForAllEngines();

        try {
            List<EngineDto> filtered = snapshot.stream()
            .filter(e -> (terminalIds == null || terminalIds.contains(e.getTerminalId())) &&
                        (engineTypeIds == null || engineTypeIds.contains(e.getEngineTypeId())))
            .toList();

            if (!filtered.isEmpty()) {
                emitter.send(SseEmitter.event()
                    .name("init")
                    .data(Map.of("engines", filtered)));
                System.out.println("Initial snapshot sent: " + filtered.size() + " engines");
            }
        } catch (Exception e) {
            System.out.println("Failed to send initial snapshot: " + e.getMessage());
        }

        // List<EngineDto> filtered = snapshot.stream()
        //     .filter(e -> (terminalIds == null || terminalIds.contains(e.getTerminalId())) &&
        //                 (engineTypeIds == null || engineTypeIds.contains(e.getEngineTypeId())))
        //     .toList();

        // if (!filtered.isEmpty()) {
        //     try {
        //         emitter.send(SseEmitter.event()
        //             .name("init")
        //             .data(filtered));
        //         System.out.println("Initial snapshot sent: " + filtered.size() + " engines");
        //     } catch (Exception e) {
        //         System.out.println("Failed to send initial snapshot: " + e.getMessage());
        //     }
        // }
    }
}
