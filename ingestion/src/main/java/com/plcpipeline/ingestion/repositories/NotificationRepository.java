package com.plcpipeline.ingestion.repositories;

import com.plcpipeline.ingestion.entities.Notification;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEngineEngineId(Long engineId);
    List<Notification> findBySeverity(String severity);
    List<Notification> findByEngineEngineCode(String engineCode);
    List<Notification> getNotificationsByTimeRange(Instant startTime, Instant endTime);
}
