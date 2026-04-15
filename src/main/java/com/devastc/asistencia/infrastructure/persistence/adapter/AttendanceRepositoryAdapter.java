package com.devastc.asistencia.infrastructure.persistence.adapter;

import com.devastc.asistencia.domain.model.Attendance;
import com.devastc.asistencia.domain.repository.AttendanceRepositoryPort;
import com.devastc.asistencia.infrastructure.mapper.AttendanceMapper;
import com.devastc.asistencia.infrastructure.persistence.entity.AttendanceEntity;
import com.devastc.asistencia.infrastructure.persistence.repository.AttendancePanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AttendanceRepositoryAdapter implements AttendanceRepositoryPort {

    @Inject
    AttendancePanacheRepository repo;

    @Override
    public Optional<Attendance> findById(UUID id) {
        return repo.findByIdOptional(id).map(AttendanceMapper::toDomain);
    }

    @Override
    public List<Attendance> findByUserId(UUID userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByProjectId(UUID projectId) {
        return repo.findByProjectId(projectId)
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByDateRange(LocalDate from, LocalDate to) {
        return repo.findByDateRange(from, to)
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return repo.findByUserAndDateRange(userId, from, to)
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByUserWithFilters(
            UUID userId,
            LocalDate from,
            LocalDate to,
            UUID projectId,
            String status,
            int page,
            int size) {
        return repo.findByUserWithFilters(userId, from, to, projectId, status, page, size)
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserWithFilters(
            UUID userId,
            LocalDate from,
            LocalDate to,
            UUID projectId,
            String status) {
        return repo.countByUserWithFilters(userId, from, to, projectId, status);
    }

    @Override
    @Transactional
    public Attendance save(Attendance a) {
        AttendanceEntity saved = repo.getEntityManager().merge(AttendanceMapper.toEntity(a));
        return AttendanceMapper.toDomain(saved);
    }

    @Override
    public long countByProjectId(UUID projectId) {
        return repo.countByProjectId(projectId);
    }

    @Override
    public List<Attendance> findAll() {
        return repo.listAll()
                .stream()
                .map(AttendanceMapper::toDomain)
                .collect(Collectors.toList());
    }
}