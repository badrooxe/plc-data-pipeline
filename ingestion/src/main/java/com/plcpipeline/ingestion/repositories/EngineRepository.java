package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Engine;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    Optional<Engine> findByCode(String code);

    // @Query("SELECT DISTINCT e.category FROM Engine e")
    // List<String> findDistinctCategories();
}