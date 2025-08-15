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
    private String type;
    private String category; //levage/roullant // to remove later
    private String family; // e.g., "family1" // to remove later
    private String ipAddress; // e.g., "192.168.1.1"
    private boolean isActive;
    private String lastSeen; // ISO-8601 string
    private String model;
    private String manufacturer;

    @ManyToOne
    @JoinColumn(name = "port_id")
    private Port port;
    
    @ManyToOne
    @JoinColumn(name = "terminal_id")
    private Terminal terminal;
}
