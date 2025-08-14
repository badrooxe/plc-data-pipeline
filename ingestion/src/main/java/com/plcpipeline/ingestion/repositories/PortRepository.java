package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
}
