package com.devastc.asistencia.infrastructure.persistence.adapter;

import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import com.devastc.asistencia.domain.model.Justification;
import com.devastc.asistencia.domain.repository.JustificationRepositoryPort;
import com.devastc.asistencia.infrastructure.mapper.JustificationMapper;
import com.devastc.asistencia.infrastructure.persistence.repository.JustificationPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class JustificationRepositoryAdapter implements JustificationRepositoryPort {

    @Inject JustificationPanacheRepository repo;

    @Override
    public Optional<Justification> findById(UUID id) {
        return repo.findByIdOptional(id).map(JustificationMapper::toDomain);
    }
    @Override
    public List<Justification> findByUserId(UUID userId) {
        return repo.findByUserId(userId).stream().map(JustificationMapper::toDomain).collect(Collectors.toList());
    }
    @Override
    public List<Justification> findByStatus(JustificationStatus status) {
        return repo.findByStatus(status.name()).stream().map(JustificationMapper::toDomain).collect(Collectors.toList());
    }
    @Override
    public List<Justification> findByUserAndStatus(UUID userId, JustificationStatus status) {
        return repo.findByUserAndStatus(userId, status.name()).stream().map(JustificationMapper::toDomain).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public Justification save(Justification j) {
        repo.getEntityManager().merge(JustificationMapper.toEntity(j)); return j;
    }
}
