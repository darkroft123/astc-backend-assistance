package com.smms.assistance.infrastructure.controller.graphql.dto;

import com.smms.assistance.domain.entity.*;
import com.smms.assistance.domain.model.*;

import java.util.List;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static AttendanceOutput toAttendanceOutput(AttendanceEntity e) {
        return AttendanceOutput.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .projectId(e.getProjectId())
                .date(e.getDate() != null ? e.getDate().toString() : null)
                .checkIn(e.getCheckIn() != null ? e.getCheckIn().toString() : null)
                .checkOut(e.getCheckOut() != null ? e.getCheckOut().toString() : null)
                .status(e.getStatus())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .photoUrl(e.getPhotoUrl())
                .build();
    }

    public static AbsenceOutput toAbsenceOutput(AbsenceEntity e) {
        return AbsenceOutput.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .projectId(e.getProjectId())
                .date(e.getDate() != null ? e.getDate().toString() : null)
                .type(e.getType())
                .justified(e.getJustified())
                .build();
    }

    public static JustificationOutput toJustificationOutput(JustificationEntity e) {
        return JustificationOutput.builder()
                .id(e.getId())
                .absenceId(e.getAbsenceId())
                .userId(e.getUserId())
                .description(e.getDescription())
                .documentUrl(e.getDocumentUrl())
                .status(e.getStatus())
                .comment(e.getComment())
                .submittedAt(e.getSubmittedAt() != null ? e.getSubmittedAt().toString() : null)
                .build();
    }

    public static JustificationStatusOutput toJustificationStatusOutput(JustificationEntity e) {
        return JustificationStatusOutput.builder()
                .justificationId(e.getId())
                .status(e.getStatus())
                .build();
    }

    public static DashboardOutput toDashboardOutput(DashboardSummary s) {
        return DashboardOutput.builder()
                .totalAttendances(s.getTotalAttendances())
                .totalAbsences(s.getTotalAbsences())
                .pendingJustifications(s.getPendingJustifications())
                .build();
    }

    public static <T> AttendancePageOutput toAttendancePage(Page<AttendanceEntity> p) {
        return AttendancePageOutput.builder()
                .items(p.getItems().stream().map(DtoMapper::toAttendanceOutput).toList())
                .page(p.getPage())
                .size(p.getSize())
                .total(p.getTotalItems())
                .build();
    }

    public static JustificationPageOutput toJustificationPage(Page<JustificationEntity> p) {
        return JustificationPageOutput.builder()
                .items(p.getItems().stream().map(DtoMapper::toJustificationOutput).toList())
                .page(p.getPage())
                .size(p.getSize())
                .total(p.getTotalItems())
                .build();
    }

    public static AbsencePageOutput toAbsencePage(Page<AbsenceEntity> p) {
        return AbsencePageOutput.builder()
                .items(p.getItems().stream().map(DtoMapper::toAbsenceOutput).toList())
                .page(p.getPage())
                .size(p.getSize())
                .total(p.getTotalItems())
                .build();
    }

    public static RegisterAttendanceOutput toRegisterAttendanceOutput(AttendanceEntity entity) {
        if (entity == null)
            return null;

        return RegisterAttendanceOutput.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .userId(entity.getUserId() != null ? entity.getUserId().toString() : null)
                .date(entity.getDate() != null ? entity.getDate().toString() : null)
                .checkIn(entity.getCheckIn() != null ? entity.getCheckIn().toString() : null)
                .status(entity.getStatus())
                .build();
    }

    public static SaveJustificationOutput toSaveJustificationOutput(JustificationEntity entity) {
        if (entity == null)
            return null;

        return SaveJustificationOutput.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt() != null ? entity.getSubmittedAt().toString() : null)
                .build();
    }
}
