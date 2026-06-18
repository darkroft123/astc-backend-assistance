package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.ApproveJustificationPort;
import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class ApproveJustificationUseCase implements ApproveJustificationPort {

    @Inject
    JustificationRepository justificationRepository;

    @Inject
    AbsenceRepository absenceRepository;

    @Override
    @Transactional
    public JustificationEntity execute(UUID justificationId, String comment, UUID reviewedBy) {
        JustificationEntity justification = justificationRepository.findByIdOptional(justificationId)
                .orElseThrow(() -> new IllegalArgumentException("Justification not found"));

        String previousStatus = justification.getStatus();
        justification.setStatus("APPROVED");
        justification.setComment(comment);
        justification.setReviewedBy(reviewedBy);
        justification.setReviewedAt(LocalDateTime.now());

        justificationRepository.persist(justification);

        // Update corresponding absence to justified = true
        AbsenceEntity absence = absenceRepository.findById(justification.getAbsenceId());
        if (absence != null) {
            absence.setJustified(true);
            absenceRepository.persist(absence);
        }

        System.out.println("[Assistance PM API] [Justification Flow] APPROVED justification -> Justification ID: " + justificationId + ", Reviewer ID: " + reviewedBy + ", Previous Status: " + previousStatus + ", New Status: APPROVED");

        return justification;
    }
}
