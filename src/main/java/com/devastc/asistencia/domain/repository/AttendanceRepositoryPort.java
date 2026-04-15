package com.devastc.asistencia.domain.repository;

import com.devastc.asistencia.domain.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepositoryPort {
        Optional<Attendance> findById(UUID id);

        List<Attendance> findByUserId(UUID userId);

        List<Attendance> findByProjectId(UUID projectId);

        List<Attendance> findByDateRange(LocalDate from, LocalDate to);

        List<Attendance> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to);

        List<Attendance> findByUserWithFilters(
                        UUID userId,
                        LocalDate from,
                        LocalDate to,
                        UUID projectId,
                        String status,
                        int page,
                        int size);

        long countByUserWithFilters(
                        UUID userId,
                        LocalDate from,
                        LocalDate to,
                        UUID projectId,
                        String status);

        Attendance save(Attendance attendance);

        long countByProjectId(UUID projectId);

        List<Attendance> findAll();
}