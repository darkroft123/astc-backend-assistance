package com.devastc.asistencia.infrastructure.persistence.repository;

import com.devastc.asistencia.infrastructure.persistence.entity.AbsenceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AbsencePanacheRepository implements PanacheRepositoryBase<AbsenceEntity, UUID> {
    public List<AbsenceEntity> findByUserId(UUID userId) {
        return list("userId", userId);
    }
    public List<AbsenceEntity> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return list("userId = ?1 and date >= ?2 and date <= ?3", userId, from, to);
    }
}
