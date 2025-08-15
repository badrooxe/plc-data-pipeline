package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.EngineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineTypeRepository extends JpaRepository<EngineType, Long> {
}
