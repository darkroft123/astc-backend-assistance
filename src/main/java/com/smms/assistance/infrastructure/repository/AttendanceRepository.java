package com.smms.assistance.infrastructure.repository;

import com.smms.assistance.domain.entity.AttendanceEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class AttendanceRepository implements PanacheRepositoryBase<AttendanceEntity, UUID> {

    public List<AttendanceEntity> findByUser(
            UUID userId,
            UUID projectId,
            LocalDate from,
            LocalDate to,
            String status,
            int page,
            int size) {

        StringBuilder query = new StringBuilder("userId = :userId");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.put("projectId", projectId);
        }
        if (from != null) {
            query.append(" and date >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" and date <= :to");
            params.put("to", to);
        }
        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        return find(query.toString(), Sort.by("date").descending(), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countByUser(
            UUID userId,
            UUID projectId,
            LocalDate from,
            LocalDate to,
            String status) {

        StringBuilder query = new StringBuilder("userId = :userId");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.put("projectId", projectId);
        }
        if (from != null) {
            query.append(" and date >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" and date <= :to");
            params.put("to", to);
        }
        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        return count(query.toString(), params);
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    public List<AttendanceEntity> findRecentByUser(UUID userId, int limit) {
        return find("userId = :userId", Sort.by("date").descending(),
                Map.of("userId", userId))
                .page(Page.of(0, limit))
                .list();
    }

    public List<AttendanceEntity> findByUsers(
            List<UUID> userIds,
            UUID projectId,
            LocalDate from,
            LocalDate to,
            String status,
            int page,
            int size) {

        System.out.println("[Assistance PM API] [DB Query] findByUsers starting -> userIds: " + userIds + ", projectId: " + projectId + ", status: " + status + ", page: " + page + ", size: " + size);

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" and userId in :userIds");
            params.put("userIds", userIds);
        } else {
            query.append(" and 1 = 0");
        }

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.put("projectId", projectId);
        }
        if (from != null) {
            query.append(" and date >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" and date <= :to");
            params.put("to", to);
        }
        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        List<AttendanceEntity> result = find(query.toString(), Sort.by("date").descending(), params)
                .page(Page.of(page, size))
                .list();

        System.out.println("[Assistance PM API] [DB Query] findByUsers completed -> matched records: " + result.size());
        return result;
    }

    public long countByUsers(
            List<UUID> userIds,
            UUID projectId,
            LocalDate from,
            LocalDate to,
            String status) {

        System.out.println("[Assistance PM API] [DB Query] countByUsers starting -> userIds: " + userIds + ", projectId: " + projectId + ", status: " + status);

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" and userId in :userIds");
            params.put("userIds", userIds);
        } else {
            return 0;
        }

        if (projectId != null) {
            query.append(" and projectId = :projectId");
            params.put("projectId", projectId);
        }
        if (from != null) {
            query.append(" and date >= :from");
            params.put("from", from);
        }
        if (to != null) {
            query.append(" and date <= :to");
            params.put("to", to);
        }
        if (status != null && !status.isBlank()) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        long count = count(query.toString(), params);
        System.out.println("[Assistance PM API] [DB Query] countByUsers completed -> total count: " + count);
        return count;
    }
}