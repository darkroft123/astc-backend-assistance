package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
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
    @Inject OutboxEventPublisher outboxPublisher;
    @Inject S3StorageAdapter s3StorageAdapter;

    @Override
@Transactional
public AttendanceEntity execute(UUID userId, RegisterAttendanceInput input) {

    UUID attendanceId = UUID.randomUUID();

    String photoUrl = null;

    if (input.getPhotoUrl() != null && !input.getPhotoUrl().isBlank()) {

        try {

            String base64 = input.getPhotoUrl();

            if (base64.contains(",")) {
                base64 = base64.split(",")[1];
            }

            byte[] decoded = Base64.getDecoder().decode(base64);

            if (decoded.length == 0) {
                throw new RuntimeException("Empty image payload");
            }

            ByteArrayInputStream stream = new ByteArrayInputStream(decoded);

            LocalDate today = LocalDate.now();

            String folder =
                    "private/user/"
                            + userId
                            + "/assistance/"
                            + today.getYear()
                            + "/"
                            + String.format("%02d", today.getMonthValue());

            String filename = attendanceId + ".jpg";

            photoUrl = s3StorageAdapter.upload(
                    folder,
                    filename,
                    stream,
                    decoded.length,
                    "image/jpeg"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error uploading photo to MinIO: " + e.getMessage(),
                    e
            );
        }
    }

    AttendanceEntity entity = AttendanceEntity.builder()
            .id(attendanceId)
            .userId(userId)
            .projectId(input.getProjectId())
            .date(LocalDate.now())
            .checkIn(LocalTime.now())
            .checkOut(null)
            .status("CHECKED_IN")
            .latitude(input.getLatitude())
            .longitude(input.getLongitude())
            .photoUrl(photoUrl)
            .build();

    attendanceRepository.persist(entity);

    outboxPublisher.publish(
            "Attendance",
            entity.getId(),
            "attendance_registered",
            String.format(
                    "{\"attendanceId\":\"%s\",\"userId\":\"%s\",\"photoUrl\":\"%s\"}",
                    entity.getId(),
                    userId,
                    photoUrl != null ? photoUrl : ""
            )
    );

    return entity;
}
}