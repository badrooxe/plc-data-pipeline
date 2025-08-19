package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Engine;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    Optional<Engine> findByCode(String code);

    // @Query("SELECT DISTINCT e.category FROM Engine e")
    // List<String> findDistinctCategories();

    @Query("SELECT e FROM Engine e WHERE e.terminal.terminalId IN :terminalIds AND e.engineType.engineTypeId IN :engineTypeIds")
    List<Engine> findByTerminalIdsAndEngineTypeIds(
        @Param("terminalIds") List<Long> terminalIds, 
        @Param("engineTypeIds") List<Long> engineTypeIds
    );
}