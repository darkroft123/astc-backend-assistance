package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.SaveJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.SaveJustificationInput;
import com.smms.assistance.infrastructure.messaging.OutboxEventPublisher;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class SaveJustificationUseCase implements SaveJustificationPort {

    @Inject JustificationRepository justificationRepository;
    @Inject AbsenceRepository absenceRepository;
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

        JustificationEntity entity = JustificationEntity.builder()
                .id(UUID.randomUUID())
                .absenceId(input.getAbsenceId())
                .userId(userId)
                .description(input.getDescription())
                .documentUrl(input.getDocumentUrl())
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();

        justificationRepository.persist(entity);

        absence.setJustified(true);
        absenceRepository.persist(absence);

        outboxPublisher.publish("Justification", entity.getId(),
                "SendNotificationByQueue",
                "{\"justificationId\":\"" + entity.getId() + "\",\"absenceId\":\"" + input.getAbsenceId() + "\",\"userId\":\"" + userId + "\"}");

        return entity;
    }
}
