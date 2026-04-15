package com.devastc.asistencia.infrastructure.persistence.repository;

import com.devastc.asistencia.infrastructure.persistence.entity.AttendanceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AttendancePanacheRepository implements PanacheRepositoryBase<AttendanceEntity, UUID> {

    public List<AttendanceEntity> findByUserId(UUID userId) {
        return list("userId", userId);
    }

    public List<AttendanceEntity> findByProjectId(UUID projectId) {
        return list("projectId", projectId);
    }

    public List<AttendanceEntity> findByDateRange(LocalDate from, LocalDate to) {
        return list("date >= ?1 and date <= ?2", from, to);
    }

    public List<AttendanceEntity> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return list("userId = ?1 and date >= ?2 and date <= ?3", userId, from, to);
    }

    public List<AttendanceEntity> findByUserWithFilters(
            UUID userId,
            LocalDate from,
            LocalDate to,
            UUID projectId,
            String status,
            int page,
            int size
    ) {
        StringBuilder query = new StringBuilder("userId = :userId and date >= :from and date <= :to");
        Parameters params = Parameters.with("userId", userId)
                .and("from", from)
                .and("to", to);

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.and("projectId", projectId);
        }

        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.and("status", status);
        }

        return find(query.toString(), params)
                .page(page, size)
                .list();
    }

    public long countByUserWithFilters(
            UUID userId,
            LocalDate from,
            LocalDate to,
            UUID projectId,
            String status
    ) {
        StringBuilder query = new StringBuilder("userId = :userId and date >= :from and date <= :to");
        Parameters params = Parameters.with("userId", userId)
                .and("from", from)
                .and("to", to);

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.and("projectId", projectId);
        }

        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.and("status", status);
        }

        return count(query.toString(), params);
    }

    public long countByProjectId(UUID projectId) {
        return count("projectId", projectId);
    }
}