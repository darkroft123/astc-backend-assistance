package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.model.AttendanceStatus;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.storage.S3StorageAdapter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class RegisterAttendanceUseCase implements RegisterAttendancePort {

    @Inject AttendanceRepository attendanceRepository;
    @Inject S3StorageAdapter storageAdapter;
    @Inject OutboxEventPublisher outboxPublisher;

    @Override
    @Transactional
    public AttendanceEntity execute(UUID userId, RegisterAttendanceInput input) {
        String photoUrl = null;
        if (input.getPhotoBase64() != null && !input.getPhotoBase64().isBlank()) {
            byte[] photoBytes = Base64.getDecoder().decode(input.getPhotoBase64());
            String filename = input.getPhotoFilename() != null ? input.getPhotoFilename() : "photo.jpg";
            photoUrl = storageAdapter.upload("attendances", filename,
                    new ByteArrayInputStream(photoBytes), photoBytes.length, "image/jpeg");
        }

        AttendanceEntity entity = AttendanceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .projectId(input.getProjectId())
                .date(input.getDate() != null ? LocalDate.parse(input.getDate()) : LocalDate.now())
                .checkIn(input.getCheckIn() != null ? LocalTime.parse(input.getCheckIn()) : null)
                .checkOut(input.getCheckOut() != null ? LocalTime.parse(input.getCheckOut()) : null)
                .status(AttendanceStatus.CHECKED_IN)
                .latitude(input.getLatitude())
                .longitude(input.getLongitude())
                .photoUrl(photoUrl)
                .build();

        attendanceRepository.persist(entity);

        outboxPublisher.publish("Attendance", entity.getId(),
                "attendance_registered",
                "{\"attendanceId\":\"" + entity.getId() + "\",\"userId\":\"" + userId + "\"}");

        return entity;
    }
}
