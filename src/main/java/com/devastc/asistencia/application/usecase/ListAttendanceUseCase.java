package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.dto.AttendanceDTO;
import com.devastc.asistencia.application.query.ListAttendanceQuery;
import com.devastc.asistencia.domain.model.Attendance;
import com.devastc.asistencia.domain.repository.AttendanceRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListAttendanceUseCase {

    @Inject
    AttendanceRepositoryPort attendanceRepository;

    public List<AttendanceDTO> execute(ListAttendanceQuery query) {
        LocalDate from = query.getFromDate() != null && !query.getFromDate().isBlank()
                ? LocalDate.parse(query.getFromDate())
                : LocalDate.now().minusMonths(1);

        LocalDate to = query.getToDate() != null && !query.getToDate().isBlank()
                ? LocalDate.parse(query.getToDate())
                : LocalDate.now();

        UUID userId = UUID.fromString(query.getUserId());
        UUID projectId = query.getProjectId() != null && !query.getProjectId().isBlank()
                ? UUID.fromString(query.getProjectId())
                : null;

        String status = query.getStatus() != null && !query.getStatus().isBlank()
                ? query.getStatus()
                : null;

        int page = Math.max(query.getPage(), 0);
        int size = query.getSize() > 0 ? query.getSize() : 10;

        List<Attendance> list = attendanceRepository.findByUserWithFilters(
                userId,
                from,
                to,
                projectId,
                status,
                page,
                size);

        return list.stream().map(a -> {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setId(a.getId() != null ? a.getId().toString() : null);
            dto.setUserId(a.getUserId() != null ? a.getUserId().toString() : null);
            dto.setProjectId(a.getProjectId() != null ? a.getProjectId().toString() : null);
            dto.setDate(a.getDate() != null ? a.getDate().toString() : null);
            dto.setCheckIn(a.getCheckIn() != null ? a.getCheckIn().toString() : null);
            dto.setCheckOut(a.getCheckOut() != null ? a.getCheckOut().toString() : null);
            dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
            dto.setLatitude(a.getLatitude());
            dto.setLongitude(a.getLongitude());
            dto.setPhotoUrl(a.getPhotoUrl());
            return dto;
        }).collect(Collectors.toList());
    }
}