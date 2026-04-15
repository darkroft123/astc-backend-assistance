package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.command.SaveJustificationCommand;
import com.devastc.asistencia.application.dto.JustificationDTO;
import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import com.devastc.asistencia.domain.model.Justification;
import com.devastc.asistencia.domain.repository.JustificationRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class SaveJustificationUseCase {
    @Inject JustificationRepositoryPort justificationRepository;

    public JustificationDTO execute(SaveJustificationCommand cmd) {
        Justification j = new Justification();
        j.setId(UUID.randomUUID());
        j.setAbsenceId(UUID.fromString(cmd.getAbsenceId()));
        j.setUserId(UUID.fromString(cmd.getUserId()));
        j.setDescription(cmd.getDescription());
        j.setDocumentUrl(cmd.getDocumentUrl());
        j.setStatus(JustificationStatus.PENDING);
        j.setSubmittedAt(LocalDateTime.now());
        Justification saved = justificationRepository.save(j);
        JustificationDTO dto = new JustificationDTO();
        dto.setId(saved.getId().toString());
        dto.setAbsenceId(saved.getAbsenceId().toString());
        dto.setStatus(saved.getStatus().name());
        dto.setSubmittedAt(saved.getSubmittedAt().toString());
        return dto;
    }
}
