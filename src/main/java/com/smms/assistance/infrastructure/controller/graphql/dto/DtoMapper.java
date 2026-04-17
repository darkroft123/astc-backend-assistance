package com.smms.assistance.infrastructure.controller.graphql.dto;

import com.smms.assistance.domain.entity.*;
import com.smms.assistance.domain.model.*;

import java.util.List;

public final class DtoMapper {

    private DtoMapper() {}

    public static AttendanceOutput toAttendanceOutput(AttendanceEntity e) {
        return AttendanceOutput.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .projectId(e.getProjectId())
                .date(e.getDate() != null ? e.getDate().toString() : null)
                .checkIn(e.getCheckIn() != null ? e.getCheckIn().toString() : null)
                .checkOut(e.getCheckOut() != null ? e.getCheckOut().toString() : null)
                .status(e.getStatus().name())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .photoUrl(e.getPhotoUrl())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                .build();
    }

    public static AbsenceOutput toAbsenceOutput(AbsenceEntity e) {
        return AbsenceOutput.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .projectId(e.getProjectId())
                .date(e.getDate() != null ? e.getDate().toString() : null)
                .type(e.getType().name())
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
                .status(e.getStatus().name())
                .comment(e.getComment())
                .submittedAt(e.getSubmittedAt() != null ? e.getSubmittedAt().toString() : null)
                .reviewedAt(e.getReviewedAt() != null ? e.getReviewedAt().toString() : null)
                .reviewedBy(e.getReviewedBy())
                .build();
    }

    public static JustificationStatusOutput toJustificationStatusOutput(JustificationEntity e) {
        return JustificationStatusOutput.builder()
                .id(e.getId())
                .status(e.getStatus().name())
                .comment(e.getComment())
                .reviewedAt(e.getReviewedAt() != null ? e.getReviewedAt().toString() : null)
                .reviewedBy(e.getReviewedBy())
                .build();
    }

    public static PageInfoOutput toPageInfo(Page<?> p) {
        return PageInfoOutput.builder()
                .totalItems(p.getTotalItems())
                .page(p.getPage())
                .size(p.getSize())
                .totalPages(p.getTotalPages())
                .build();
    }

    public static DashboardOutput toDashboardOutput(DashboardSummary s) {
        return DashboardOutput.builder()
                .totalAttendances(s.getTotalAttendances())
                .totalAbsences(s.getTotalAbsences())
                .totalJustifications(s.getTotalJustifications())
                .recentAttendances(s.getRecentAttendances().stream().map(DtoMapper::toAttendanceOutput).toList())
                .recentJustifications(s.getRecentJustifications().stream().map(DtoMapper::toJustificationOutput).toList())
                .build();
    }

    public static <T> AttendancePageOutput toAttendancePage(Page<AttendanceEntity> p) {
        return AttendancePageOutput.builder()
                .items(p.getItems().stream().map(DtoMapper::toAttendanceOutput).toList())
                .pageInfo(toPageInfo(p))
                .build();
    }

    public static JustificationPageOutput toJustificationPage(Page<JustificationEntity> p) {
        return JustificationPageOutput.builder()
                .items(p.getItems().stream().map(DtoMapper::toJustificationOutput).toList())
                .pageInfo(toPageInfo(p))
                .build();
    }
}
