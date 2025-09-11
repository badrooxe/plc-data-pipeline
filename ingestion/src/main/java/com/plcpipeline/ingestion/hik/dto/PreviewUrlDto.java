package com.plcpipeline.ingestion.hik.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Safe DTO that contains only the useful URLs for the front-end.

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviewUrlDto {
    private String engineCode;
    private List<String> urls;
}
