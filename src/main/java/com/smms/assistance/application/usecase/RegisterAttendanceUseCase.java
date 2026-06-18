package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.storage.S3StorageAdapter;
import com.smms.assistance.infrastructure.client.ProjectManagerClientWrapper;
import com.smms.assistance.infrastructure.client.ProjectOutput;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    @Transactional
    public AttendanceEntity execute(UUID userId, RegisterAttendanceInput input) {

        java.time.LocalDateTime timestamp = java.time.LocalDateTime.now();
        System.out.println("📝 [Audit Log] Attendance registration attempt - userId: " + userId 
                + ", projectId: " + input.getProjectId() 
                + ", timestamp: " + timestamp);

        List<ProjectOutput> assignedProjects;
        try {
            assignedProjects = pmClientWrapper.getMyProjects();
        } catch (Exception e) {
            System.out.println("❌ [Audit Log] Attendance registration failed - userId: " + userId 
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
            System.out.println("❌ [Audit Log] Validation failure: projectId " + input.getProjectId() 
                    + " is not part of the user's assigned projects. userId: " + userId);
            System.out.println("❌ [Audit Log] Attendance registration failed - userId: " + userId 
                    + ", projectId: " + input.getProjectId() 
                    + ", timestamp: " + timestamp 
                    + ", reason: Project ID is not assigned to user");
            throw new IllegalArgumentException("El proyecto no está asignado a este usuario.");
        }

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

    System.out.println("✅ [Audit Log] Attendance registration successful - userId: " + userId 
            + ", projectId: " + input.getProjectId() 
            + ", timestamp: " + timestamp 
            + ", attendanceId: " + entity.getId());

    // --- ORCHESTRATE STATELESS ALERTS ANALYSIS ---
    try {
        if (matchedProject != null) {
            System.out.println("📝 [Orchestrator] Match result: Project details retrieved successfully for projectId: " + input.getProjectId());
            
            String dateStr = LocalDate.now().toString();
            String checkInStr = LocalTime.now().toString();
            
            var alertPayload = new com.smms.assistance.infrastructure.client.AlertRequestPayload(
                entity.getId(),
                userId,
                entity.getProjectId(),
                dateStr,
                checkInStr,
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getPhotoUrl(),
                matchedProject.getLatitude(),
                matchedProject.getLongitude(),
                matchedProject.getRadius(),
                matchedProject.getWorkStartTime(),
                matchedProject.getWorkEndTime(),
                matchedProject.getGraceMinutes()
            );

            System.out.println("📝 [Orchestrator] Payload generated for Alerts Service. Sending asynchronously...");
            
            // Invoke the Alerts Service (AI engine) via client
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
        // MUST NOT break attendance flow
        System.out.println("❌ [Orchestrator] Error from AI service or alert persistence: " + e.getMessage());
        e.printStackTrace();
    }

    return entity;
}
}