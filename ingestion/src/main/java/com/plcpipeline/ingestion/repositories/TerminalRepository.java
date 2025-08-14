package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
    List<Terminal> findByPort_PortId(Long portId);  // Find all terminals by port id
}
