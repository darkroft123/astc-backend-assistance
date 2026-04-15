package com.devastc.asistencia.infrastructure.persistence.repository;

import com.devastc.asistencia.infrastructure.persistence.entity.JustificationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JustificationPanacheRepository implements PanacheRepositoryBase<JustificationEntity, UUID> {
    public List<JustificationEntity> findByUserId(UUID userId) {
        return list("userId", userId);
    }
    public List<JustificationEntity> findByStatus(String status) {
        return list("status", status);
    }
    public List<JustificationEntity> findByUserAndStatus(UUID userId, String status) {
        return list("userId = ?1 and status = ?2", userId, status);
    }
}
