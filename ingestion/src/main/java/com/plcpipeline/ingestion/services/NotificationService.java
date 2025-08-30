package com.plcpipeline.ingestion.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.N;
import org.springframework.stereotype.Service;

import com.plcpipeline.ingestion.dtos.NotificationDto;
import com.plcpipeline.ingestion.entities.Engine;
import com.plcpipeline.ingestion.entities.Notification;
import com.plcpipeline.ingestion.exceptions.ResourceNotFoundException;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.EngineRepository;
import com.plcpipeline.ingestion.repositories.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EngineRepository engineRepository;

    public NotificationService(NotificationRepository notificationRepository, EngineRepository engineRepository) {
        this.notificationRepository = notificationRepository;
        this.engineRepository = engineRepository;
    }

    public List<NotificationDto> getAllNotifications(){
        return notificationRepository
            .findAll()
            .stream()
            .map(Mapper::toNotificationDto)
            .toList();
    }

    public List<NotificationDto> getNotificationsByTimeRange(Instant startTime, Instant endTime) {
        if (startTime.isAfter(endTime)) {
        throw new IllegalArgumentException("Start time must be before end time");
        }
        return notificationRepository.getNotificationsByTimeRange(startTime, endTime)
                .stream()
                .map(Mapper::toNotificationDto)
                .collect(Collectors.toList());
    }

    public NotificationDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));
        return Mapper.toNotificationDto(notification);
    }

    public List<NotificationDto> getNotificationsByEngineId(Long engineId) {
        Engine engine = engineRepository.findById(engineId)
                .orElseThrow(() -> new ResourceNotFoundException("Engine not found with id " + engineId));
        return notificationRepository.findByEngineEngineId(engineId)
                .stream()
                .map(Mapper::toNotificationDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsByEngineCode(String engineCode) {
        return notificationRepository.findByEngineEngineCode(engineCode)
                .stream()
                .map(Mapper::toNotificationDto)
                .collect(Collectors.toList());
    }

    public NotificationDto createNotification(NotificationDto notificationDto){
        Engine engine = engineRepository.findById(notificationDto.getEngineId())
            .orElseThrow(() -> new ResourceNotFoundException("Engine not found with id " + notificationDto.getEngineId()));
        Notification notification = Mapper.toNotificationEntity(notificationDto, engine);
        notificationRepository.save(notification);
        return Mapper.toNotificationDto(notification);
    }

    public NotificationDto updateNotification(Long id, NotificationDto dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
        Engine engine = engineRepository.findById(dto.getEngineId())
                .orElseThrow(() -> new RuntimeException("Engine not found with id " + dto.getEngineId()));
        notification.setMessage(dto.getMessage());
        notification.setSeverity(dto.getSeverity());
        notification.setTimestamp(dto.getTimestamp());
        notification.setTriggerValue(dto.getTriggerValue());
        notification.setEngine(engine);
        notification = notificationRepository.save(notification);
        return Mapper.toNotificationDto(notification);
    }
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
