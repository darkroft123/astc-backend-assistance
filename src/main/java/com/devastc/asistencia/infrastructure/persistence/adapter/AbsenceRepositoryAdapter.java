package com.devastc.asistencia.infrastructure.persistence.adapter;

import com.devastc.asistencia.domain.model.Absence;
import com.devastc.asistencia.domain.repository.AbsenceRepositoryPort;
import com.devastc.asistencia.infrastructure.mapper.AbsenceMapper;
import com.devastc.asistencia.infrastructure.persistence.entity.AbsenceEntity;
import com.devastc.asistencia.infrastructure.persistence.repository.AbsencePanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AbsenceRepositoryAdapter implements AbsenceRepositoryPort {

    @Inject
    AbsencePanacheRepository repo;

    @Override
    public Optional<Absence> findById(UUID id) {
        return repo.findByIdOptional(id).map(AbsenceMapper::toDomain);
    }

    @Override
    public List<Absence> findByUserId(UUID userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(AbsenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Absence> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return repo.findByUserAndDateRange(userId, from, to)
                .stream()
                .map(AbsenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Absence save(Absence a) {
        AbsenceEntity saved = repo.getEntityManager().merge(AbsenceMapper.toEntity(a));
        return AbsenceMapper.toDomain(saved);
    }
}