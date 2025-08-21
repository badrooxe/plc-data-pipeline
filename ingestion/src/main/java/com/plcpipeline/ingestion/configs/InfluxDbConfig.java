package com.plcpipeline.ingestion.configs;

import com.influxdb.v3.client.InfluxDBClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private char[] token;

    @Value("${influxdb.database}")
    private String database;

    @Bean(destroyMethod = "close")
    public InfluxDBClient influxDBClient() {
        return InfluxDBClient.getInstance(url, token, database);
    }
}