package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.SaveJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.model.JustificationStatus;
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
    @Inject S3StorageAdapter storageAdapter;
    @Inject OutboxEventPublisher outboxPublisher;

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

        String documentUrl = null;
        if (input.getDocumentBase64() != null && !input.getDocumentBase64().isBlank()) {
            byte[] docBytes = Base64.getDecoder().decode(input.getDocumentBase64());
            String filename = input.getDocumentFilename() != null ? input.getDocumentFilename() : "document.pdf";
            documentUrl = storageAdapter.upload("justifications", filename,
                    new ByteArrayInputStream(docBytes), docBytes.length, "application/octet-stream");
        }

        JustificationEntity entity = JustificationEntity.builder()
                .id(UUID.randomUUID())
                .absenceId(input.getAbsenceId())
                .userId(userId)
                .description(input.getDescription())
                .documentUrl(documentUrl)
                .status(JustificationStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        justificationRepository.persist(entity);

        absence.setJustified(true);
        absenceRepository.persist(absence);

        outboxPublisher.publish("Justification", entity.getId(),
                "justification_saved",
                "{\"justificationId\":\"" + entity.getId() + "\",\"absenceId\":\"" + input.getAbsenceId() + "\",\"userId\":\"" + userId + "\"}");

        return entity;
    }
}
