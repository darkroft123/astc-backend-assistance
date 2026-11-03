package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.EditJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.entity.JustificationHistoryEntity;
import com.smms.assistance.infrastructure.controller.graphql.dto.SaveJustificationInput;
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
public class EditJustificationUseCase implements EditJustificationPort {

    @Inject JustificationRepository justificationRepository;
    @Inject AbsenceRepository absenceRepository;
    @Inject S3StorageAdapter s3StorageAdapter;

    @Override
    @Transactional
    public JustificationEntity execute(UUID userId, UUID justificationId, SaveJustificationInput input) {
        if (justificationId == null) {
            throw new IllegalArgumentException("El ID de la justificación es obligatorio.");
        }

        if (input.getDescription() != null) {
            String desc = input.getDescription().trim();
            if (desc.length() > 500) {
                throw new IllegalArgumentException("El motivo de la justificación no puede exceder los 500 caracteres.");
            }
            if (desc.toLowerCase().contains("<script") || desc.toLowerCase().contains("javascript:")) {
                throw new IllegalArgumentException("El motivo contiene texto o caracteres no permitidos.");
            }
        }

        JustificationEntity justification = justificationRepository.findByIdOptional(justificationId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la justificación a editar: " + justificationId));

        if (!justification.getUserId().equals(userId)) {
            throw new SecurityException("Acceso denegado: la justificación no pertenece al usuario autenticado.");
        }

        String currentStatus = justification.getStatus();
        if (!"PENDING".equals(currentStatus) && !"OBSERVATION".equals(currentStatus)) {
            throw new IllegalStateException("No se puede editar una justificación con estado: " + currentStatus + ". Solo se permiten justificaciones pendientes u observadas.");
        }

        if (input.getDescription() != null && !input.getDescription().isBlank()) {
            justification.setDescription(input.getDescription());
        }

        String rawBase64 = input.getFileBase64();
        if ((rawBase64 == null || rawBase64.isBlank()) && input.getDocumentUrl() != null && (input.getDocumentUrl().startsWith("data:") || input.getDocumentUrl().length() > 500)) {
            rawBase64 = input.getDocumentUrl();
        }

        if (rawBase64 != null && !rawBase64.isBlank()) {
            try {
                String raw = rawBase64;
                if (raw.contains(",")) {
                    raw = raw.substring(raw.indexOf(",") + 1);
                }
                byte[] decoded = Base64.getDecoder().decode(raw);
                String fileUuid = UUID.randomUUID().toString();
                String folder = "document/private/user/" + userId + "/justifications";
                String filename = fileUuid + ".pdf";
                String key = s3StorageAdapter.upload(folder, filename,
                        new ByteArrayInputStream(decoded), decoded.length, "application/octet-stream");
                justification.setDocumentUrl(key);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload file to MinIO", e);
            }
        }

        justificationRepository.persist(justification);

        System.out.println("[Justification Flow] EDITED -> ID: " + justificationId);

        return justification;
    }
}
