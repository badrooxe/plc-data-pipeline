package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEngine_EngineId(Long engineId);
    List<Notification> findBySeverity(String severity);
    List<Notification> findByEngine_Code(String engineCode);
    List<Notification> findByTimestampBetween(Long startTime, Long endTime);
}
