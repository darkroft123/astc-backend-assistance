package com.smms.assistance.application.usecase;

import com.smms.assistance.domain.entity.AbsenceEntity;
//import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class GenerateAbsenceIfNeededUseCase {

    private static final LocalTime LIMIT_TIME = LocalTime.of(9, 30);

    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    AttendanceRepository attendanceRepository;

    @Transactional
    public void execute(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (now.isBefore(LIMIT_TIME)) {
            return;
        }

        boolean hasAttendance = attendanceRepository
                .find("userId = ?1 and date = ?2", userId, today)
                .firstResultOptional()
                .isPresent();

        boolean hasAbsence = absenceRepository
                .find("userId = ?1 and date = ?2", userId, today)
                .firstResultOptional()
                .isPresent();

        if (hasAttendance || hasAbsence) {
            return;
        }

        AbsenceEntity absence = AbsenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .projectId(null)
                .date(today)
                .type("UNJUSTIFIED")
                .justified(false)
                .build();

        absenceRepository.persist(absence);
    }
}