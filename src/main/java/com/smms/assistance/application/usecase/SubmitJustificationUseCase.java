package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.SubmitJustificationPort;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.entity.JustificationHistoryEntity;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class SubmitJustificationUseCase implements SubmitJustificationPort {

    @Inject
    JustificationRepository justificationRepository;

    @Override
    @Transactional
    public JustificationEntity execute(UUID userId, UUID justificationId) {
        JustificationEntity entity = justificationRepository.findByIdOptional(justificationId)
                .orElseThrow(() -> new IllegalArgumentException("Justification not found: " + justificationId));

        if (!entity.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: justification does not belong to you");
        }

        String currentStatus = entity.getStatus();
        if (!"PENDING".equals(currentStatus) && !"OBSERVATION".equals(currentStatus)) {
            throw new IllegalStateException("Cannot submit justification with status: " + currentStatus);
        }

        String previousStatus = currentStatus;
        entity.setStatus("SUBMITTED");
        entity.setSubmittedAt(java.time.Instant.now().atZone(java.time.ZoneOffset.UTC).toLocalDateTime());
        justificationRepository.persist(entity);

        recordHistory(justificationId, previousStatus, "SUBMITTED", null, userId);

        System.out.println("[Justification Flow] SUBMITTED -> " + previousStatus + " -> SUBMITTED -> ID: " + justificationId);
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
