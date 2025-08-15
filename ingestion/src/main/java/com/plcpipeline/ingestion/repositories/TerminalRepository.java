package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Terminal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
    List<Terminal> findByPort_PortId(Long portId);  // Find all terminals by port id
    //Optional<Terminal> findByTerminalIdAndPortId(Long terminalId, Long portId);  // Find terminal by terminal id and port id
}
