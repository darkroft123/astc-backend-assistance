package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.storage.S3StorageAdapter;
import com.smms.assistance.infrastructure.client.ProjectManagerClientWrapper;
import com.smms.assistance.infrastructure.client.ProjectOutput;
import com.smms.assistance.shared.util.TimezoneService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.List;

@ApplicationScoped
public class RegisterAttendanceUseCase implements RegisterAttendancePort {

    @Inject AttendanceRepository attendanceRepository;
    @Inject OutboxEventPublisher outboxPublisher;
    @Inject S3StorageAdapter s3StorageAdapter;
    @Inject ProjectManagerClientWrapper pmClientWrapper;
    @Inject com.smms.assistance.infrastructure.client.AlertsClient alertsClient;
    @Inject AbsenceRepository absenceRepository;
    @Inject org.eclipse.microprofile.jwt.JsonWebToken jwt;

    @Override
    @Transactional
    public AttendanceEntity execute(UUID userId, RegisterAttendanceInput input) {

        java.time.LocalDateTime timestamp = TimezoneService.utcNow();
        System.out.println("[Audit Log] Attendance registration attempt - userId: " + userId 
                + ", projectId: " + input.getProjectId() 
                + ", timestamp: " + timestamp);

        List<ProjectOutput> assignedProjects;
        try {
            assignedProjects = pmClientWrapper.getMyProjects();
        } catch (Exception e) {
            System.out.println("[Audit Log] Attendance registration failed - userId: " + userId 
                    + ", projectId: " + input.getProjectId() 
                    + ", timestamp: " + timestamp 
                    + ", reason: Failed to retrieve assigned projects from Project Manager");
            throw new RuntimeException("Could not retrieve assigned projects: " + e.getMessage(), e);
        }

        boolean isAssigned = false;
        ProjectOutput matchedProject = null;
        if (assignedProjects != null && input.getProjectId() != null) {
            for (ProjectOutput project : assignedProjects) {
                if (project.id != null && project.id.equals(input.getProjectId())) {
                    isAssigned = true;
                    matchedProject = project;
                    break;
                }
            }
        }

        if (!isAssigned) {
            System.out.println("[Audit Log] Validation failure: projectId " + input.getProjectId() 
                    + " is not part of the user's assigned projects. userId: " + userId);
            System.out.println("[Audit Log] Attendance registration failed - userId: " + userId 
                    + ", projectId: " + input.getProjectId() 
                    + ", timestamp: " + timestamp 
                    + ", reason: Project ID is not assigned to user");
            throw new IllegalArgumentException("El proyecto no esta asignado a este usuario.");
        }

        // SINGLE TIME SOURCE -- Project's configured timezone
        String projectTimezone = (matchedProject != null && matchedProject.getTimezone() != null)
                ? matchedProject.getTimezone()
                : TimezoneService.defaultTimezone();
        ZonedDateTime nowInProjectZone = TimezoneService.zonedNow(projectTimezone);
        LocalDate today = nowInProjectZone.toLocalDate();
        LocalTime nowTime = nowInProjectZone.toLocalTime();

        // Step 1: Check if attendance already exists for today
        boolean existsToday = attendanceRepository
                .find("userId = ?1 and projectId = ?2 and date = ?3", userId, input.getProjectId(), today)
                .firstResultOptional()
                .isPresent();

        System.out.println("[Attendance] EXISTS_TODAY=" + existsToday
                + " | userId=" + userId + " projectId=" + input.getProjectId() + " date=" + today);

        // Step 2: If exists, evaluate replace flag BEFORE any error
        if (existsToday) {
            boolean shouldReplace = Boolean.TRUE.equals(input.getReplace());
            System.out.println("[Attendance] REPLACE_ENABLED=" + shouldReplace);

            if (shouldReplace) {
                long deleted = attendanceRepository.delete(
                        "userId = ?1 and projectId = ?2 and date = ?3",
                        userId, input.getProjectId(), today
                );
                System.out.println("[Attendance] DELETED_AND_RECREATED | deleted=" + deleted
                        + " records for userId=" + userId);
            } else {
                throw new IllegalStateException("Ya registraste tu asistencia para este proyecto hoy.");
            }
        }

        // Classify attendance based on effective schedule (backend only)
        String attendanceStatus = classifyAttendance(matchedProject, nowTime);
        System.out.println("[Attendance] CLASSIFICATION=" + attendanceStatus
                + " | checkIn=" + nowTime
                + " | workStart=" + (matchedProject != null ? matchedProject.getWorkStartTime() : "null")
                + " | grace=" + (matchedProject != null ? matchedProject.getGraceMinutes() : 0));

        UUID attendanceId = UUID.randomUUID();

        String photoUrl = null;
        String alertsPhotoUrl = null;

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

                String folder =
                        "private/user/"
                                + userId
                                + "/assistance/"
                                + today.getYear()
                                + "/"
                                + String.format("%02d", today.getMonthValue());

                String filename = attendanceId + ".jpg";

                String key = s3StorageAdapter.upload(
                        folder,
                        filename,
                        stream,
                        decoded.length,
                        "image/jpeg"
                );

                photoUrl = key;
                alertsPhotoUrl = s3StorageAdapter.generatePresignedUrl(key, 120);

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
                .date(today)
                .checkIn(nowTime)
                .checkOut(null)
                .status(attendanceStatus)
                .latitude(input.getLatitude())
                .longitude(input.getLongitude())
                .photoUrl(photoUrl)
                .projectTimezone(matchedProject != null && matchedProject.getTimezone() != null
                        ? matchedProject.getTimezone() : TimezoneService.defaultTimezone())
                .build();

        attendanceRepository.persist(entity);

        // Auto-generate absence when check-in is outside the allowed schedule window
        if ("OUTSIDE_SCHEDULE".equals(attendanceStatus) || "EARLY".equals(attendanceStatus) || "LATE".equals(attendanceStatus)) {
            boolean hasAbsence = absenceRepository
                    .find("userId = ?1 and projectId = ?2 and date = ?3", userId, input.getProjectId(), today)
                    .firstResultOptional()
                    .isPresent();
            if (!hasAbsence) {
                AbsenceEntity absence = AbsenceEntity.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .projectId(input.getProjectId())
                        .date(today)
                        .type("FALTA_AUTOMATICA")
                        .justified(false)
                        .build();
                absenceRepository.persist(absence);
                System.out.println("[Attendance] ABSENCE auto-generated for " + attendanceStatus + " check-in — userId="
                        + userId + ", projectId=" + input.getProjectId() + ", date=" + today);
            }
        }

        String outboxPhotoUrl = alertsPhotoUrl != null ? alertsPhotoUrl : photoUrl;
        String userEmail = jwt != null ? jwt.getClaim("email") : null;
        String userName = jwt != null ? jwt.getClaim("name") : null;
        if (userEmail == null || userEmail.isBlank()) {
            userEmail = "";
        }
        if (userName == null || userName.isBlank()) {
            userName = userEmail.contains("@") ? userEmail.split("@")[0] : "Colaborador";
        }
        userEmail = userEmail.replace("\"", "\\\"");
        userName = userName.replace("\"", "\\\"");

        outboxPublisher.publish(
                "Attendance",
                entity.getId(),
                "attendance_registered",
                String.format(
                        "{\"attendanceId\":\"%s\",\"userId\":\"%s\",\"photoUrl\":\"%s\",\"userEmail\":\"%s\",\"userName\":\"%s\"}",
                        entity.getId(),
                        userId,
                        outboxPhotoUrl != null ? outboxPhotoUrl : "",
                        userEmail,
                        userName
                )
        );

        System.out.println("✅ [Audit Log] Attendance registration successful - userId: " + userId 
                + ", projectId: " + input.getProjectId() 
                + ", status: " + attendanceStatus
                + ", checkIn: " + entity.getCheckIn()
                + ", timestamp: " + timestamp 
                + ", attendanceId: " + entity.getId());

        // --- ORCHESTRATE STATELESS ALERTS ANALYSIS ---
        try {
            if (matchedProject != null) {
                System.out.println("📝 [Orchestrator] Match result: Project details retrieved successfully for projectId: " + input.getProjectId());
                
                String dateStr = today.toString();
                String checkInStr = nowTime.toString();
                
                var alertPayload = new com.smms.assistance.infrastructure.client.AlertRequestPayload(
                    entity.getId(),
                    userId,
                    entity.getProjectId(),
                    dateStr,
                    checkInStr,
                    entity.getLatitude(),
                    entity.getLongitude(),
                    alertsPhotoUrl != null ? alertsPhotoUrl : entity.getPhotoUrl(),
                    matchedProject.getLatitude(),
                    matchedProject.getLongitude(),
                    matchedProject.getRadius(),
                    matchedProject.getWorkStartTime(),
                    matchedProject.getWorkEndTime(),
                    matchedProject.getGraceMinutes()
                );

                System.out.println("📝 [Orchestrator] Payload generated for Alerts Service. Sending asynchronously...");
                
                com.smms.assistance.infrastructure.client.AlertResponsePayload aiResult = alertsClient.sendAlert(alertPayload);
                
                if (aiResult != null) {
                    System.out.println("📝 [Orchestrator] Response received from Alerts Service: alert=" + aiResult.alert);
                    if (aiResult.alert) {
                        System.out.println("🚨 [Orchestrator] Decision: alert created. Calling Project Manager CreateAlert mutation...");
                        pmClientWrapper.createAlert(
                            userId,
                            entity.getProjectId(),
                            aiResult.type != null ? aiResult.type : "UNKNOWN",
                            aiResult.message != null ? aiResult.message : "AI validation flagged anomaly",
                            entity.getLatitude(),
                            entity.getLongitude()
                        );
                    } else {
                        System.out.println("✅ [Orchestrator] Decision: alert ignored (no anomaly detected).");
                    }
                }
            } else {
                System.out.println("⚠️ [Orchestrator] Project details not found during matching. Skipping alert analysis.");
            }
        } catch (Exception e) {
            System.out.println("❌ [Orchestrator] Error from AI service or alert persistence: " + e.getMessage());
            e.printStackTrace();
        }

        return entity;
    }

    private String classifyAttendance(ProjectOutput project, LocalTime checkIn) {
        if (project == null || project.getWorkStartTime() == null) {
            System.out.println("[Classification] No schedule defined. Defaulting to CHECKED_IN.");
            return "CHECKED_IN";
        }

        try {
            LocalTime workStart = LocalTime.parse(project.getWorkStartTime());
            int grace = project.getGraceMinutes() != null ? project.getGraceMinutes() : 0;
            LocalTime deadline = workStart.plusMinutes(grace);

            LocalTime workEnd = null;
            if (project.getWorkEndTime() != null && !project.getWorkEndTime().isBlank()) {
                workEnd = LocalTime.parse(project.getWorkEndTime());
            }

            // Before schedule window (e.g. earlier than 2 hours before work start)
            LocalTime earliestAllowed = workStart.minusHours(2);
            if (checkIn.isBefore(earliestAllowed)) {
                System.out.println("[Classification] EARLY — checkIn=" + checkIn
                        + ", workStart=" + workStart + ", earliestAllowed=" + earliestAllowed
                        + ", workEnd=" + workEnd + ", graceMinutes=" + grace);
                return "EARLY";
            }

            // After schedule window (if workEnd is defined)
            if (workEnd != null && checkIn.isAfter(workEnd)) {
                System.out.println("[Classification] OUTSIDE_SCHEDULE — checkIn=" + checkIn
                        + ", workStart=" + workStart + ", workEnd=" + workEnd
                        + ", graceMinutes=" + grace);
                return "OUTSIDE_SCHEDULE";
            }

            // Within schedule window: classify on-time vs late
            if (checkIn.equals(deadline) || checkIn.isBefore(deadline)) {
                System.out.println("[Classification] ON_TIME — checkIn=" + checkIn
                        + ", workStart=" + workStart + ", deadline=" + deadline
                        + ", workEnd=" + workEnd + ", graceMinutes=" + grace);
                return "ON_TIME";
            } else {
                System.out.println("[Classification] LATE — checkIn=" + checkIn
                        + ", workStart=" + workStart + ", deadline=" + deadline
                        + ", workEnd=" + workEnd + ", graceMinutes=" + grace);
                return "LATE";
            }
        } catch (Exception e) {
            System.out.println("[Classification] Error parsing schedule times: " + e.getMessage()
                    + ". Defaulting to CHECKED_IN.");
            return "CHECKED_IN";
        }
    }
}