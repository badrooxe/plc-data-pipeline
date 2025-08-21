package com.plcpipeline.ingestion.services;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.PointValues;
import com.influxdb.v3.client.query.QueryOptions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Stream;

@Service
public class InfluxDbTestService {

    private final InfluxDBClient influxDBClient;

    public InfluxDbTestService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void testConnectivity() {
        try {
            // ✅ Step 1: Write test point
            Point point = Point.measurement("temperature")
                    .setTag("location", "west")
                    .setField("value", 55.15)
                    .setTimestamp(Instant.now());

            influxDBClient.writePoint(point);
            System.out.println("✅ Successfully wrote point to InfluxDB.");

            // ✅ Step 2: Query data back as Points
            String sql = "select time,location,value from temperature order by time desc limit 5";

            try (Stream<PointValues> stream = influxDBClient.queryPoints(sql, QueryOptions.DEFAULTS)) {
                stream.forEach(p -> {
                    var time = p.getTimestamp();
                    var location = p.getTag("location");
                    var value = p.getField("value", Double.class);

                    System.out.printf("🔎 Row -> location=%s, value=%.2f, time=%s%n",
                            location, value, time);
                });
            }

        } catch (Exception e) {
            System.err.println("❌ InfluxDB test failed: " + e.getMessage());
        }
    }
}
