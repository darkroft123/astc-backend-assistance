package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.dto.DashboardDTO;
import com.devastc.asistencia.application.query.GetDashboardQuery;
import com.devastc.asistencia.domain.repository.AttendanceRepositoryPort;
import com.devastc.asistencia.domain.repository.AbsenceRepositoryPort;
import com.devastc.asistencia.domain.repository.JustificationRepositoryPort;
import com.devastc.asistencia.domain.enumtype.JustificationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class GetDashboardUseCase {
    @Inject AttendanceRepositoryPort attendanceRepository;
    @Inject AbsenceRepositoryPort absenceRepository;
    @Inject JustificationRepositoryPort justificationRepository;

    public DashboardDTO execute(GetDashboardQuery query) {
        UUID userId = UUID.fromString(query.getUserId());
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalAttendances(attendanceRepository.findByUserId(userId).size());
        dto.setTotalAbsences(absenceRepository.findByUserId(userId).size());
        dto.setPendingJustifications(justificationRepository.findByUserAndStatus(userId, JustificationStatus.PENDING).size());
        return dto;
    }
}
