package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
}