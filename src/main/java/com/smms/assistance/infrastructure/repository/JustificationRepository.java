package com.smms.assistance.infrastructure.repository;

import com.smms.assistance.domain.entity.JustificationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class JustificationRepository implements PanacheRepositoryBase<JustificationEntity, UUID> {

    public List<JustificationEntity> findByUser(UUID userId, String status,
            LocalDate from, LocalDate to,
            int page, int size) {
        StringBuilder query = new StringBuilder("userId = :userId");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }
        if (from != null) {
            query.append(" and COALESCE(submittedAt, createdAt) >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            query.append(" and COALESCE(submittedAt, createdAt) <= :to");
            params.put("to", to.plusDays(1).atStartOfDay());
        }

        return find(query.toString(), Sort.by("createdAt").descending(), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countByUser(UUID userId, String status, LocalDate from, LocalDate to) {
        StringBuilder query = new StringBuilder("userId = :userId");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }
        if (from != null) {
            query.append(" and COALESCE(submittedAt, createdAt) >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            query.append(" and COALESCE(submittedAt, createdAt) <= :to");
            params.put("to", to.plusDays(1).atStartOfDay());
        }

        return count(query.toString(), params);
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    public List<JustificationEntity> findRecentByUser(UUID userId, int limit) {
        return find("userId = :userId", Sort.by("submittedAt").descending(),
                Map.of("userId", userId))
                .page(Page.of(0, limit))
                .list();
    }

    public Optional<JustificationEntity> findByAbsenceId(UUID absenceId) {
        return find("absenceId", absenceId).firstResultOptional();
    }

    public List<JustificationEntity> findByUsers(
            List<UUID> userIds,
            String status,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {

        System.out.println("[Assistance PM API] [DB Query] findByUsers (justifications) starting -> userIds: " + userIds + ", status: " + status + ", page: " + page + ", size: " + size);

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" and userId in :userIds");
            params.put("userIds", userIds);
        } else {
            query.append(" and 1 = 0");
        }

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }
        if (from != null) {
            query.append(" and COALESCE(submittedAt, createdAt) >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            query.append(" and COALESCE(submittedAt, createdAt) <= :to");
            params.put("to", to.plusDays(1).atStartOfDay());
        }

        List<JustificationEntity> result = find(query.toString(), Sort.by("createdAt").descending(), params)
                .page(Page.of(page, size))
                .list();

        System.out.println("[Assistance PM API] [DB Query] findByUsers (justifications) completed -> matched records: " + result.size());
        return result;
    }

    public long countByUsers(
            List<UUID> userIds,
            String status,
            LocalDate from,
            LocalDate to) {

        System.out.println("[Assistance PM API] [DB Query] countByUsers (justifications) starting -> userIds: " + userIds + ", status: " + status);

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" and userId in :userIds");
            params.put("userIds", userIds);
        } else {
            return 0;
        }

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }
        if (from != null) {
            query.append(" and COALESCE(submittedAt, createdAt) >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            query.append(" and COALESCE(submittedAt, createdAt) <= :to");
            params.put("to", to.plusDays(1).atStartOfDay());
        }

        long count = count(query.toString(), params);
        System.out.println("[Assistance PM API] [DB Query] countByUsers (justifications) completed -> total count: " + count);
        return count;
    }
}