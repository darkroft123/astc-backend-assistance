package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.dto.JustificationDTO;
import com.devastc.asistencia.application.query.ListJustificationsQuery;
import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import com.devastc.asistencia.domain.model.Justification;
import com.devastc.asistencia.domain.repository.JustificationRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListJustificationsUseCase {
    @Inject JustificationRepositoryPort justificationRepository;

    public List<JustificationDTO> execute(ListJustificationsQuery query) {
        List<Justification> list;
        if (query.getStatusFilter() != null && !query.getStatusFilter().isEmpty()) {
            if (query.getUserId() != null) {
                list = justificationRepository.findByUserAndStatus(UUID.fromString(query.getUserId()), JustificationStatus.valueOf(query.getStatusFilter()));
            } else {
                list = justificationRepository.findByStatus(JustificationStatus.valueOf(query.getStatusFilter()));
            }
        } else if (query.getUserId() != null) {
            list = justificationRepository.findByUserId(UUID.fromString(query.getUserId()));
        } else {
            list = justificationRepository.findByStatus(JustificationStatus.PENDING);
        }
        return list.stream().map(j -> {
            JustificationDTO dto = new JustificationDTO();
            dto.setId(j.getId().toString());
            dto.setAbsenceId(j.getAbsenceId().toString());
            dto.setUserId(j.getUserId().toString());
            dto.setDescription(j.getDescription());
            dto.setDocumentUrl(j.getDocumentUrl());
            dto.setStatus(j.getStatus().name());
            dto.setComment(j.getComment());
            dto.setSubmittedAt(j.getSubmittedAt() != null ? j.getSubmittedAt().toString() : null);
            return dto;
        }).collect(Collectors.toList());
    }
}
