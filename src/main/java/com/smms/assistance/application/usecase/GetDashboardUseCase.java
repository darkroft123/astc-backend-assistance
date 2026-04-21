package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.GetDashboardPort;
import com.smms.assistance.domain.model.DashboardSummary;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class GetDashboardUseCase implements GetDashboardPort {

    @Inject
    AttendanceRepository attendanceRepository;
    @Inject
    AbsenceRepository absenceRepository;
    @Inject
    JustificationRepository justificationRepository;

    @Override
    public DashboardSummary execute(UUID userId, LocalDate fromDate, LocalDate toDate) {
        return DashboardSummary.builder()
                .totalAttendances((int) attendanceRepository.countByUser(
                        userId,
                        null,
                        fromDate,
                        toDate,
                        null))
                .totalAbsences((int) absenceRepository.countByUser(
                        userId,
                        null,
                        fromDate,
                        toDate,
                        null,
                        null))
                .pendingJustifications((int) justificationRepository.countByUser(
                        userId,
                        "PENDING",
                        fromDate,
                        toDate))
                .build();
    }
}