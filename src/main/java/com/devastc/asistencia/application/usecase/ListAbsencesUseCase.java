package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.dto.AbsenceDTO;
import com.devastc.asistencia.application.query.ListAbsencesQuery;
import com.devastc.asistencia.domain.model.Absence;
import com.devastc.asistencia.domain.repository.AbsenceRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListAbsencesUseCase {

    @Inject
    AbsenceRepositoryPort absenceRepository;

    public List<AbsenceDTO> execute(ListAbsencesQuery query) {
        LocalDate from = query.getFromDate() != null && !query.getFromDate().isBlank()
                ? LocalDate.parse(query.getFromDate())
                : LocalDate.now().minusMonths(1);

        LocalDate to = query.getToDate() != null && !query.getToDate().isBlank()
                ? LocalDate.parse(query.getToDate())
                : LocalDate.now();

        List<Absence> list = absenceRepository.findByUserAndDateRange(
                UUID.fromString(query.getUserId()),
                from,
                to);

        return list.stream().map(a -> {
            AbsenceDTO dto = new AbsenceDTO();
            dto.setId(a.getId() != null ? a.getId().toString() : null);
            dto.setUserId(a.getUserId() != null ? a.getUserId().toString() : null);
            dto.setProjectId(a.getProjectId() != null ? a.getProjectId().toString() : null);
            dto.setDate(a.getDate() != null ? a.getDate().toString() : null);
            dto.setType(a.getType() != null ? a.getType().name() : null);
            dto.setJustified(a.isJustified());
            return dto;
        }).collect(Collectors.toList());
    }
}