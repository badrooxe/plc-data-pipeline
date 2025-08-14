package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Engine;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    Optional<Engine> findByCode(String code);
}