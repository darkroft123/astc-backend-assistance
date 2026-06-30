package com.smms.assistance.application.usecase;

import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.shared.util.TimezoneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
public class GenerateAbsenceIfNeededUseCase {

    @ConfigProperty(name = "app.attendance.absence-cutoff-hour", defaultValue = "9")
    int cutoffHour;

    @ConfigProperty(name = "app.attendance.absence-cutoff-minute", defaultValue = "30")
    int cutoffMinute;

    @ConfigProperty(name = "app.attendance.default-timezone", defaultValue = "UTC")
    String defaultTimezone;

    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    AttendanceRepository attendanceRepository;

    @Transactional
    public void execute(UUID userId) {
        String tz = TimezoneService.isValidTimezone(defaultTimezone) ? defaultTimezone : TimezoneService.defaultTimezone();
        ZonedDateTime nowInZone = TimezoneService.zonedNow(tz);
        LocalDate today = nowInZone.toLocalDate();
        LocalTime now = nowInZone.toLocalTime();
        LocalTime cutoff = LocalTime.of(cutoffHour, cutoffMinute);

        if (now.isBefore(cutoff)) {
            System.out.println("[AbsenceGen] Before cutoff (" + cutoff + "). Skipping absence check for userId=" + userId);
            return;
        }

        boolean hasAttendance = attendanceRepository
                .find("userId = ?1 and date = ?2 and status not in (?3, ?4)",
                        userId, today, "OUTSIDE_SCHEDULE", "EARLY")
                .firstResultOptional()
                .isPresent();

        boolean hasAbsence = absenceRepository
                .find("userId = ?1 and date = ?2", userId, today)
                .firstResultOptional()
                .isPresent();

        if (hasAttendance || hasAbsence) {
            System.out.println("[AbsenceGen] User already has attendance or absence for today. Skipping. userId=" + userId);
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

        System.out.println("[AbsenceGen] ABSENCE generated for userId=" + userId
                + ", date=" + today + ", cutoff=" + cutoff + ". No attendance found.");
    }
}