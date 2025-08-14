package com.plcpipeline.ingestion.dtos;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TelemetryDataDto {
    private Long terminalId;
    private String engineId;
    private Long portId;
    private String timestamp;

    private String variableName;
    private Object value;

    @JsonAnySetter
    public void captureVariable(String key, Object val) {
        if (!key.equals("terminalId") && !key.equals("engineId") && 
            !key.equals("portId") && !key.equals("timestamp")) {
            this.variableName = key;
            this.value = val;
        }
    }
}


// Uncomment the following code if you want to use a map for multiple variables

// public class TelemetryDataDto {
//     private Long terminalId;
//     private String engineId;
//     private Long portId;
//     private Map<String, Object> variables; // key = variable_name, value = value
//     private String timestamp; // ISO-8601 string
// }


// {
//   "terminalId": 1,
//   "engineId": "bed78dc",
//   "portId": 1,
//   "variables": {
//     "rpm": 1450.5,
//     "temperature": 88.2,
//     "isActive": true
//   },
//   "timestamp": "2025-08-13T12:30:45Z"
// }

