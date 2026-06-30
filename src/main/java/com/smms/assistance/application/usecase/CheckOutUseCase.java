package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.CheckOutPort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.client.ProjectManagerClientWrapper;
import com.smms.assistance.infrastructure.client.ProjectOutput;
import com.smms.assistance.shared.util.TimezoneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CheckOutUseCase implements CheckOutPort {

    @Inject
    AttendanceRepository attendanceRepository;

    @Inject
    ProjectManagerClientWrapper pmClientWrapper;

    @Override
    @Transactional
    public AttendanceEntity execute(UUID userId, UUID projectId) {
        String projectTimezone = resolveProjectTimezone(userId, projectId);
        ZonedDateTime nowInProjectZone = TimezoneService.zonedNow(projectTimezone);
        LocalDate today = nowInProjectZone.toLocalDate();
        LocalTime nowTime = nowInProjectZone.toLocalTime();

        Optional<AttendanceEntity> existing = attendanceRepository
                .find("userId = ?1 and projectId = ?2 and date = ?3", userId, projectId, today)
                .firstResultOptional();

        if (existing.isEmpty()) {
            throw new IllegalStateException("No hay registro de entrada para hoy. Registre su entrada primero.");
        }

        AttendanceEntity entity = existing.get();

        if (entity.getCheckOut() != null) {
            throw new IllegalStateException("Ya registraste tu salida hoy.");
        }

        entity.setCheckOut(nowTime);

        String departureStatus = classifyDeparture(userId, projectId, entity.getStatus(), nowTime);
        entity.setStatus(departureStatus);

        attendanceRepository.persist(entity);

        System.out.println("[Attendance] CHECK-OUT -> userId=" + userId
                + ", projectId=" + projectId
                + ", entry=" + entity.getCheckIn()
                + ", exit=" + entity.getCheckOut()
                + ", status=" + entity.getStatus());

        return entity;
    }

    /**
     * Classify check-out against project schedule.
     * If check-out is before workEndTime and the entry was ON_TIME → "EARLY_DEPARTURE".
     * Otherwise preserves the original status (ON_TIME, LATE, etc.).
     * If no schedule is defined, appends "_CHECKED_OUT" to existing status.
     */
    private String classifyDeparture(UUID userId, UUID projectId, String currentStatus, LocalTime checkOutTime) {
        try {
            java.util.List<ProjectOutput> projects = pmClientWrapper.getMyProjects();
            if (projects == null) return currentStatus + "_CHECKED_OUT";

            ProjectOutput matched = null;
            for (ProjectOutput p : projects) {
                if (p.id != null && p.id.equals(projectId)) {
                    matched = p;
                    break;
                }
            }

            if (matched == null || matched.getWorkEndTime() == null || matched.getWorkEndTime().isBlank()) {
                System.out.println("[CheckOut] No workEndTime defined. Status unchanged: " + currentStatus);
                return currentStatus + "_CHECKED_OUT";
            }

            LocalTime workEnd = LocalTime.parse(matched.getWorkEndTime());

            if (checkOutTime.isBefore(workEnd)) {
                System.out.println("[CheckOut] EARLY_DEPARTURE — checkOut=" + checkOutTime
                        + ", workEnd=" + workEnd);
                return "EARLY_DEPARTURE";
            }

            System.out.println("[CheckOut] On-time departure — checkOut=" + checkOutTime
                    + ", workEnd=" + workEnd);
            return currentStatus + "_CHECKED_OUT";

        } catch (Exception e) {
            System.out.println("[CheckOut] Error classifying departure: " + e.getMessage());
            return currentStatus + "_CHECKED_OUT";
        }
    }

    private String resolveProjectTimezone(UUID userId, UUID projectId) {
        try {
            java.util.List<ProjectOutput> projects = pmClientWrapper.getMyProjects();
            if (projects != null) {
                for (ProjectOutput p : projects) {
                    if (p.id != null && p.id.equals(projectId) && p.getTimezone() != null
                            && TimezoneService.isValidTimezone(p.getTimezone())) {
                        return p.getTimezone();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[CheckOut] Could not resolve project timezone, using default");
        }
        return TimezoneService.defaultTimezone();
    }
}
