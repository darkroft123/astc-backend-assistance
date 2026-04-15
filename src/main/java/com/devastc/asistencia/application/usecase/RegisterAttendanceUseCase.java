package com.devastc.asistencia.application.usecase;

import com.devastc.asistencia.application.command.RegisterAttendanceCommand;
import com.devastc.asistencia.application.dto.AttendanceDTO;
import com.devastc.asistencia.domain.enumtype.AttendanceStatus;
import com.devastc.asistencia.domain.model.Attendance;
import com.devastc.asistencia.domain.repository.AttendanceRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class RegisterAttendanceUseCase {

    private static final Logger LOG = Logger.getLogger(RegisterAttendanceUseCase.class);

    @Inject
    AttendanceRepositoryPort attendanceRepository;

    public AttendanceDTO execute(RegisterAttendanceCommand cmd) {
        LOG.infof("Registering attendance for user: %s", cmd.getUserId());

        Attendance a = new Attendance();
        a.setId(UUID.randomUUID());
        a.setUserId(UUID.fromString(cmd.getUserId()));
        a.setProjectId(cmd.getProjectId() != null && !cmd.getProjectId().isBlank()
                ? UUID.fromString(cmd.getProjectId())
                : null);
        a.setDate(LocalDate.now());
        a.setCheckIn(LocalTime.now());
        a.setStatus(AttendanceStatus.PRESENT);
        a.setLatitude(cmd.getLatitude());
        a.setLongitude(cmd.getLongitude());
        a.setPhotoUrl(cmd.getPhotoUrl());

        Attendance saved = attendanceRepository.save(a);

        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(saved.getId() != null ? saved.getId().toString() : null);
        dto.setUserId(saved.getUserId() != null ? saved.getUserId().toString() : null);
        dto.setProjectId(saved.getProjectId() != null ? saved.getProjectId().toString() : null);
        dto.setDate(saved.getDate() != null ? saved.getDate().toString() : null);
        dto.setCheckIn(saved.getCheckIn() != null ? saved.getCheckIn().toString() : null);
        dto.setCheckOut(saved.getCheckOut() != null ? saved.getCheckOut().toString() : null);
        dto.setStatus(saved.getStatus() != null ? saved.getStatus().name() : null);
        dto.setLatitude(saved.getLatitude());
        dto.setLongitude(saved.getLongitude());
        dto.setPhotoUrl(saved.getPhotoUrl());

        return dto;
    }
}