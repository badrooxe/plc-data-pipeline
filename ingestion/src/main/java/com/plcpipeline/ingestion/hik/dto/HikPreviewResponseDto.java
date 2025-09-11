package com.plcpipeline.ingestion.hik.dto;

import java.util.List;
import lombok.Data;

@Data
public class HikPreviewResponseDto {
    private String code;
    private String msg;
    private PreviewData data;

    @Data
    public static class PreviewData {
        private List<PreviewItem> list;
    }

    @Data
    public static class PreviewItem {
        private String url;
        private String authentication; // optional
    }
}
