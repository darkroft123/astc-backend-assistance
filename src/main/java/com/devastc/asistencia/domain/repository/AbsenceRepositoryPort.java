package com.devastc.asistencia.domain.repository;

import com.devastc.asistencia.domain.model.Absence;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbsenceRepositoryPort {
    Optional<Absence> findById(UUID id);

    List<Absence> findByUserId(UUID userId);

    List<Absence> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to);

    Absence save(Absence absence);
}