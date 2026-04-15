package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.command.JustifyAbsenceCommand;
import com.devastc.asistencia.domain.model.Absence;
import com.devastc.asistencia.domain.repository.AbsenceRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class JustifyAbsenceUseCase {

    @Inject
    AbsenceRepositoryPort absenceRepository;

    public boolean execute(JustifyAbsenceCommand cmd) {

        Absence absence = absenceRepository.findById(
                UUID.fromString(cmd.getAbsenceId())).orElseThrow(() -> new RuntimeException("Absence not found"));

        if (!absence.getUserId().toString().equals(cmd.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        absence.setJustified(true);

        absenceRepository.save(absence);

        return true;
    }
}