package com.plcpipeline.ingestion.dtos;

//import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EngineDto {
    private Long engineId;

    @NotBlank(message = "Engine code cannot be blank.")
    @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters.")
    private String code;

    @NotBlank(message = "Engine name cannot be blank.")
    private String name;
    //private String type;
    //private String category;
    //private String family;

    //@Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", message = "Invalid IP address format.")
    private String ipAddress;

    //@JsonProperty("isActive") // Use @JsonProperty to map the field correctly in JSON
    private boolean isActive;
    private String lastSeen; // ISO-8601 string
    //private String model;
    //private String manufacturer;
    private Long hours;
    private Integer notificationCount;

    @NotNull(message = "portId is required.")
    private Long portId;

    @NotNull(message = "terminalId is required.")
    private Long terminalId;

    @NotNull(message = "engineTypeId is required.")
    private Long engineTypeId;
}