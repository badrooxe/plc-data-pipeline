package com.plcpipeline.ingestion.hik.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class HikPreviewRequestDto {
    private List<String> cameraIndexCodes;
    private int streamType;
    private String protocol;
    private int transmode;
}