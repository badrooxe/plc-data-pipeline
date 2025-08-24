package com.plcpipeline.ingestion.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long engineId;
    private String code; // e.g., "bed78dc"
    private String name;// e.g., "terex"
    //private String type; //to remove later
    //private String category; //levage/roullant // to remove later
    //private String family; // e.g., "family1" // to remove later
    private String ipAddress; // e.g., "192.168.1.1"
    private boolean isActive;
    private String lastSeen; // ISO-8601 string
    //private String model; //to remiove later
    //private String manufacturer;
    // private Double temperature;
    private Long hours;
    private Integer notificationCount;

    @ManyToOne
    @JoinColumn(name = "engine_type_id", nullable = false)
    private EngineType engineType;
        
    @ManyToOne
    @JoinColumn(name = "terminal_id", nullable = false)
    private Terminal terminal;

    @ManyToOne
    @JoinColumn(name = "port_id", nullable = false)
    private Port port;

}
