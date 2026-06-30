package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.DeleteJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class DeleteJustificationUseCase implements DeleteJustificationPort {

    @Inject JustificationRepository justificationRepository;
    @Inject AbsenceRepository absenceRepository;

    @Override
    @Transactional
    public void execute(UUID userId, UUID justificationId) {
        JustificationEntity justification = justificationRepository.findByIdOptional(justificationId)
                .orElseThrow(() -> new IllegalArgumentException("Justification not found: " + justificationId));

        if (!justification.getUserId().equals(userId)) {
            throw new SecurityException("Access denied: justification does not belong to authenticated user");
        }

        if (!"PENDING".equals(justification.getStatus())) {
            throw new IllegalStateException("Cannot delete justification with status: " + justification.getStatus());
        }

        UUID absenceId = justification.getAbsenceId();

        justificationRepository.delete(justification);

        AbsenceEntity absence = absenceRepository.findById(absenceId);
        if (absence != null) {
            absence.setJustified(false);
            absenceRepository.persist(absence);
        }

        System.out.println("[Justification Flow] DELETED justification -> Justification ID: " + justificationId + ", Absence ID: " + absenceId);
    }
}