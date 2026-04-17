package com.smms.assistance.infrastructure.repository;

import com.smms.assistance.domain.entity.AbsenceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AbsenceRepository implements PanacheRepositoryBase<AbsenceEntity, UUID> {

    public List<AbsenceEntity> findByUserId(UUID userId) {
        return list("userId", userId);
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }
}
