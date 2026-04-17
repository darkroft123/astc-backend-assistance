package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.ListAbsencesPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListAbsencesUseCase implements ListAbsencesPort {

    @Inject AbsenceRepository absenceRepository;

    @Override
    public List<AbsenceEntity> execute(UUID userId) {
        return absenceRepository.findByUserId(userId);
    }
}
