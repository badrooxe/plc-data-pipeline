package com.plcpipeline.ingestion.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String severity; // e.g., "info", "warning", "critical"
    private String message; // e.g., "Engine temperature high"
    private Long timestamp; // epoch time in milliseconds
    private Integer triggerValue; // e.g., 85 for temperature threshold

    @ManyToOne
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;
}
