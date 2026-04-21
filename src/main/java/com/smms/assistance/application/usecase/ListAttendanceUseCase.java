package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.ListAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.model.Page;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListAttendanceUseCase implements ListAttendancePort {

    @Inject
    AttendanceRepository attendanceRepository;

    @Override
    public Page<AttendanceEntity> execute(
            UUID userId,
            int page,
            int size,
            UUID projectId,
            String fromDate,
            String toDate,
            String status) {

        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;

        List<AttendanceEntity> items = attendanceRepository.findByUser(
                userId,
                projectId,
                from,
                to,
                status,
                page,
                size);

        long total = attendanceRepository.countByUser(
                userId,
                projectId,
                from,
                to,
                status);

        return new Page<>(items, total, page, size);
    }
}