package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.SaveJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.entity.JustificationHistoryEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.SaveJustificationInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import com.smms.assistance.infrastructure.storage.S3StorageAdapter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class SaveJustificationUseCase implements SaveJustificationPort {

    @Inject JustificationRepository justificationRepository;
    @Inject AbsenceRepository absenceRepository;
    @Inject OutboxEventPublisher outboxPublisher;
    @Inject S3StorageAdapter s3StorageAdapter;

    @Override
    @Transactional
    public JustificationEntity execute(UUID userId, SaveJustificationInput input) {
        AbsenceEntity absence = absenceRepository.findById(input.getAbsenceId());
        if (absence == null) {
            throw new IllegalArgumentException("Absence not found: " + input.getAbsenceId());
        }
        if (!absence.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: absence does not belong to authenticated user");
        }

        justificationRepository.findByAbsenceId(input.getAbsenceId()).ifPresent(j -> {
            throw new IllegalStateException("A justification already exists for absence: " + input.getAbsenceId());
        });

        String documentUrl = input.getDocumentUrl();

        if (input.getFileBase64() != null && !input.getFileBase64().isBlank()) {
            try {
                String raw = input.getFileBase64();
                if (raw.contains(",")) {
                    raw = raw.substring(raw.indexOf(",") + 1);
                }
                byte[] decoded = Base64.getDecoder().decode(raw);
                String fileUuid = UUID.randomUUID().toString();
                String folder = "document/private/user/" + userId + "/justifications";
                String filename = fileUuid + ".pdf";
                String key = s3StorageAdapter.upload(folder, filename,
                        new ByteArrayInputStream(decoded), decoded.length, "application/octet-stream");
                documentUrl = key;
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload file to MinIO", e);
            }
        }

        UUID justificationId = UUID.randomUUID();
        JustificationEntity entity = JustificationEntity.builder()
                .id(justificationId)
                .absenceId(input.getAbsenceId())
                .userId(userId)
                .description(input.getDescription())
                .documentUrl(documentUrl)
                .status("PENDING")
                .submittedAt(null)
                .build();

        justificationRepository.persist(entity);

        recordHistory(justificationId, null, "PENDING", "Justification created", userId);

        outboxPublisher.publish("Justification", entity.getId(),
                "SendNotificationByQueue",
                "{\"justificationId\":\"" + entity.getId() + "\",\"absenceId\":\"" + input.getAbsenceId() + "\",\"userId\":\"" + userId + "\"}");

        System.out.println("[Justification Flow] CREATED -> ID: " + justificationId + ", Status: PENDING");
        return entity;
    }

    private void recordHistory(UUID justificationId, String previousStatus, String newStatus,
                                String comment, UUID changedBy) {
        JustificationHistoryEntity history = JustificationHistoryEntity.builder()
                .id(UUID.randomUUID())
                .justificationId(justificationId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .comment(comment)
                .changedBy(changedBy)
                .changedAt(java.time.Instant.now().atZone(java.time.ZoneOffset.UTC).toLocalDateTime())
                .build();
        history.persist();
    }
}
