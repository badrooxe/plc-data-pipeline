package com.plcpipeline.ingestion.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    // batch buffer
    private final List<Point> buffer = Collections.synchronizedList(new ArrayList<>());
    private final int BATCH_SIZE = 500; // adjust as needed
    private final long FLUSH_INTERVAL_MS = 60000; // flush every 60s if not full

    // In-memory latest state map (engineCode → EngineDto)
    private final ConcurrentHashMap<String, EngineDto> latestStateMap = new ConcurrentHashMap<>();

    public EngineSseService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;

        // Start a background thread to flush the buffer periodically
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::flush, FLUSH_INTERVAL_MS, FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
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
        writeAllTelemetryPoint(engine, telemetry);

        // Step 2: Convert the updated Engine entity to a DTO for the SSE payload
        //EngineDto updatedEngineDto = Mapper.toEngineDto(engine);
        //List<EngineDto> latestStates = getLatestStateForAllEngines();
        EngineDto updatedEngine = new EngineDto();
        updatedEngine.setEngineId(engine.getEngineId());
        updatedEngine.setCode(telemetry.getEngineCode());
        updatedEngine.setName(telemetry.getEngineName());
        updatedEngine.setIpAddress(telemetry.getIpAddress());
        updatedEngine.setLastSeen(telemetry.getTimestamp());
        updatedEngine.setPortId(telemetry.getPortId());
        updatedEngine.setTerminalId(telemetry.getTerminalId());
        updatedEngine.setEngineTypeId(telemetry.getEngineTypeId());
        updatedEngine.setActive((Boolean) telemetry.getValue().get("État d'alimentation de l'entraînement du moteur d'orientation (1 = On)"));
        updatedEngine.setHours(((Number) telemetry.getValue().get("Compteur d'heures de service total de la grue")).longValue());
        //updatedEngine.setNotificationCount(((Number) telemetry.getValue().get("notificationCount")).intValue());

        latestStateMap.put(telemetry.getEngineCode(), updatedEngine);

        // Step 3: Broadcast the DTO to all connected clients
        //sendEngineUpdate(updatedEngineDto);
        sendEngineUpdate(List.of(updatedEngine));
    }

    private void writeTelemetryPoint(Engine engine, TelemetryDataDto telemetry) {
        // Implement the logic to write telemetry data to InfluxDB
        Map<String, Object> variables = telemetry.getValue();
        if (variables == null || variables.isEmpty()) {
            //System.out.println("No variables found in telemetry data. Skipping InfluxDB write.");
            return;
        }

        try {
            // Full history measurement
            Point point = Point.measurement("engine_telemetry2")
                .setField("engineId", engine.getEngineId())
                .setField("engineCode", telemetry.getEngineCode())
                .setField("engineName", telemetry.getEngineName())
                .setField("ipAddress", telemetry.getIpAddress())
                .setField("lastSeen", telemetry.getTimestamp() != null ? telemetry.getTimestamp() : Instant.now().toString())
                .setField("portId", telemetry.getPortId())
                .setField("terminalId", telemetry.getTerminalId())
                .setField("engineTypeId", telemetry.getEngineTypeId())
                .setField("isActive", telemetry.getValue().get("État d'alimentation de l'entraînement du moteur d'orientation (1 = On)"))
                .setField("hours", telemetry.getValue().get("Compteur d'heures de service total de la grue"));
                //.setField("notificationCount", telemetry.getValue().get("notificationCount"))
                //.setTimestamp(Instant.parse(telemetry.getTimestamp()));

            buffer.add(point);
            //buffer.add(latestPoint);
            if (buffer.size() >= BATCH_SIZE) {
                flush();
            }

        } catch (Exception e) {
            System.out.println("Failed to write telemetry data to InfluxDB: " + e.getMessage());
        }   
    }

    private void writeAllTelemetryPoint(Engine engine, TelemetryDataDto telemetry) {
        Map<String, Object> variables = telemetry.getValue();
        if (variables == null || variables.isEmpty()) {
            //System.out.println("No variables found in telemetry data. Skipping InfluxDB write.");
            return;
        }

        try {
            // Full history measurement
            Point point = Point.measurement("engine_all_telemetry")
                .setField("engineId", engine.getEngineId())
                .setField("engineCode", telemetry.getEngineCode())
                .setField("engineName", telemetry.getEngineName())
                .setField("ipAddress", telemetry.getIpAddress())
                .setField("lastSeen", telemetry.getTimestamp() != null ? telemetry.getTimestamp() : Instant.now().toString())
                .setField("portId", telemetry.getPortId())
                .setField("terminalId", telemetry.getTerminalId())
                .setField("engineTypeId", telemetry.getEngineTypeId());
                //.setTimestamp(Instant.parse(telemetry.getTimestamp()));
                // .setField("isActive", telemetry.getValue().get("État d'alimentation de l'entraînement du moteur d'orientation (1 = On)"))
                // .setField("hours", telemetry.getValue().get("Compteur d'heures de service total de la grue"))
                // .setField("notificationCount", telemetry.getValue().get("notificationCount"))
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    String varName = entry.getKey();
                    Object varValue = entry.getValue();
                    if (varValue == null) continue;
                    else if (varValue instanceof Number) {
                        point.setField(varName, ((Number) varValue).doubleValue());
                    } else if (varValue instanceof Boolean) {
                        point.setField(varName, (Boolean) varValue);
                    } else if (varValue instanceof String) {
                        point.setField(varName, (String) varValue);
                    }
                }

            Point point2 = Point.measurement("engine_all_telemetry_json")
                .setField("engineId", engine.getEngineId())
                .setField("engineCode", telemetry.getEngineCode())
                .setField("engineName", telemetry.getEngineName())
                .setField("ipAddress", telemetry.getIpAddress())
                .setField("lastSeen", telemetry.getTimestamp() != null ? telemetry.getTimestamp() : Instant.now().toString())
                .setField("portId", telemetry.getPortId())
                .setField("terminalId", telemetry.getTerminalId())
                .setField("engineTypeId", telemetry.getEngineTypeId())
                //.setTimestamp(Instant.parse(telemetry.getTimestamp()))
                // set the full value object as a JSON string field
                .setField("values", new ObjectMapper().writeValueAsString(variables));

            buffer.add(point);
            buffer.add(point2);
            if (buffer.size() >= BATCH_SIZE) {
                flush();
            }

        } catch (Exception e) {
            System.out.println("Failed to write telemetry data to InfluxDB: " + e.getMessage());
        }
    }

    public List<EngineDto> getLatestStateForAllEngines() {

        //measure approximate network transfer time
        Long latency = testInfluxDBConnection();
        System.out.println("-----------------------Approximate network latency to InfluxDB: " + latency + " ms");

        String sql = "SELECT \"engineId\", \"engineCode\", \"engineName\", \"ipAddress\", \"lastSeen\", " +
                 "\"portId\", \"terminalId\", \"engineTypeId\", \"isActive\", \"hours\", \"notificationCount\" " +
                 "FROM engine_telemetry2 " +
                 "WHERE time IN (SELECT MAX(time) FROM engine_telemetry2 GROUP BY \"engineCode\")";

        long start = System.nanoTime();
        try (Stream<Object[]> stream = influxDBClient.query(sql)) {
            stream.forEach(row -> {
                EngineDto engineDto = new EngineDto();
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

                latestStateMap.put(engineDto.getCode(), engineDto);
            });
        }
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("-----------------------Startup latest-state query took: " + durationMs + " ms");

        return new ArrayList<>(latestStateMap.values());
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
    }

    private synchronized void flush() {
        if (buffer.isEmpty()) return;

        Long latency = testInfluxDBConnection();
        System.out.println("-----------------------Approximate network latency to InfluxDB: " + latency + " ms");

        Long start = System.nanoTime();
        try {
            influxDBClient.writePoints(new ArrayList<>(buffer));

            Long durationMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("------------------------Influx write of " + buffer.size() + " points took: " + durationMs + " ms");
            
            buffer.clear();
            System.out.println("✅ Flushed batch to InfluxDB");
        } catch (Exception e) {
            Long durationMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("❌ Failed to flush batch: " + e.getMessage() + " (took " + durationMs + " ms)");
        }
    }

    //custom function to test to test influx connectivity and measure query time
    public Long testInfluxDBConnection() {
        Long startNetwork = System.nanoTime();
        try(Stream<Object[]> testStream = influxDBClient.query("SELECT 1")){
            testStream.forEach(row -> {
                // do nothing, just testing connectivity
            });
        }
        Long durationNetworkMs = (System.nanoTime() - startNetwork) / 1_000_000;
        //System.out.println("InfluxDB connectivity test query took: " + durationNetworkMs + " ms");
        return durationNetworkMs;
    }

}