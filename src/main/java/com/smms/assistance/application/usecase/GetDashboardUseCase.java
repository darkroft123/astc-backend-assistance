package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.GetDashboardPort;
import com.smms.assistance.domain.model.DashboardSummary;
import com.smms.assistance.infrastructure.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class GetDashboardUseCase implements GetDashboardPort {

    @Inject AttendanceRepository attendanceRepository;
    @Inject AbsenceRepository absenceRepository;
    @Inject JustificationRepository justificationRepository;

    @Override
    public DashboardSummary execute(UUID userId) {
        return DashboardSummary.builder()
                .totalAttendances(attendanceRepository.countByUserId(userId))
                .totalAbsences(absenceRepository.countByUserId(userId))
                .totalJustifications(justificationRepository.countByUserId(userId))
                .recentAttendances(attendanceRepository.findRecentByUser(userId, 5))
                .recentJustifications(justificationRepository.findRecentByUser(userId, 5))
                .build();
    }
}
