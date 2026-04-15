package com.devastc.asistencia.domain.repository;

import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import com.devastc.asistencia.domain.model.Justification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JustificationRepositoryPort {
    Optional<Justification> findById(UUID id);
    List<Justification> findByUserId(UUID userId);
    List<Justification> findByStatus(JustificationStatus status);
    List<Justification> findByUserAndStatus(UUID userId, JustificationStatus status);
    Justification save(Justification justification);
}
