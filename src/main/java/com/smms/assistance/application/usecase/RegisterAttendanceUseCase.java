package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class RegisterAttendanceUseCase implements RegisterAttendancePort {

    @Inject AttendanceRepository attendanceRepository;
    @Inject OutboxEventPublisher outboxPublisher;

    @Override
    @Transactional
    public AttendanceEntity execute(UUID userId, RegisterAttendanceInput input) {
        AttendanceEntity entity = AttendanceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .projectId(input.getProjectId())
                .date(LocalDate.now())
                .checkIn(LocalTime.now())
                .checkOut(null)
                .status("CHECKED_IN")
                .latitude(input.getLatitude())
                .longitude(input.getLongitude())
                .photoUrl(input.getPhotoUrl())
                .build();

        attendanceRepository.persist(entity);

        outboxPublisher.publish("Attendance", entity.getId(),
                "attendance_registered",
                "{\"attendanceId\":\"" + entity.getId() + "\",\"userId\":\"" + userId + "\"}");

        return entity;
    }
}
