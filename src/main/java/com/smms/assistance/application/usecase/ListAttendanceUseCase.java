package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.ListAttendancePort;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.model.AttendanceStatus;
import com.smms.assistance.domain.model.Page;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListAttendanceUseCase implements ListAttendancePort {

    @Inject AttendanceRepository attendanceRepository;

    @Override
    public Page<AttendanceEntity> execute(UUID userId, LocalDate from, LocalDate to,
                                           AttendanceStatus status, String search,
                                           int page, int size) {
        List<AttendanceEntity> items = attendanceRepository.findByUser(userId, from, to, status, search, page, size);
        long total = attendanceRepository.countByUser(userId, from, to, status);
        int totalPages = (int) Math.ceil((double) total / size);

        return Page.<AttendanceEntity>builder()
                .items(items)
                .totalItems(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }
}
