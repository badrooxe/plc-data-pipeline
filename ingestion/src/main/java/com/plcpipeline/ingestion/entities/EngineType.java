package com.plcpipeline.ingestion.entities;

import jakarta.persistence.Column;
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
@Table(name = "engine_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EngineType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long engineTypeId;

    @Column(unique = true, nullable = false)
    private String name; // "CHARGEUSE GRANDE CAPACITE", "Levage Mobiles", etc.
    private String family; // 'terex', 'liebherr', etc.
    private String model; // e.g., "terex-rt-780"
    private String icon; // e.g., "bi-exclamation-triangle-fill"

    // An EngineType can have multiple Accessories.
    // @ManyToOne
    // @JoinColumn(name = "accessory_id", nullable

    // An EngineType belongs to one Category.
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
