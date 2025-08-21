package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.services.InfluxDbTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfluxDbTestController {

    private final InfluxDbTestService testService;

    public InfluxDbTestController(InfluxDbTestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test-influx")
    public String testInflux() {
        testService.testConnectivity();
        return "InfluxDB test executed. Check logs.";
    }
}
