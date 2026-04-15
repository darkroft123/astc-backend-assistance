package com.devastc.asistencia.presentation.graphql.mapper;

import com.devastc.asistencia.application.dto.*;
import com.devastc.asistencia.presentation.graphql.output.*;

public class GraphQLMapper {

    public static AttendanceOutput toOutput(AttendanceDTO dto) {
        AttendanceOutput o = new AttendanceOutput();
        o.setId(dto.getId());
        o.setUserId(dto.getUserId());
        o.setProjectId(dto.getProjectId());
        o.setDate(dto.getDate());
        o.setCheckIn(dto.getCheckIn());
        o.setCheckOut(dto.getCheckOut());
        o.setStatus(dto.getStatus());
        o.setLatitude(dto.getLatitude());
        o.setLongitude(dto.getLongitude());
        o.setPhotoUrl(dto.getPhotoUrl());
        return o;
    }

    public static AbsenceOutput toOutput(AbsenceDTO dto) {
        AbsenceOutput o = new AbsenceOutput();
        o.setId(dto.getId());
        o.setUserId(dto.getUserId());
        o.setProjectId(dto.getProjectId());
        o.setDate(dto.getDate());
        o.setType(dto.getType());
        o.setJustified(dto.getJustified());
        return o;
    }

    public static JustificationOutput toOutput(JustificationDTO dto) {
        if (dto == null)
            return null;

        JustificationOutput o = new JustificationOutput();
        o.setId(dto.getId());
        o.setAbsenceId(dto.getAbsenceId());
        o.setUserId(dto.getUserId());
        o.setDescription(dto.getDescription());
        o.setDocumentUrl(dto.getDocumentUrl());
        o.setStatus(dto.getStatus());
        o.setComment(dto.getComment());
        o.setSubmittedAt(dto.getSubmittedAt());
        o.setReviewedAt(dto.getReviewedAt());

        return o;
    }

    public static DashboardOutput toOutput(DashboardDTO dto) {
        DashboardOutput o = new DashboardOutput();
        o.setTotalAttendances(dto.getTotalAttendances());
        o.setTotalAbsences(dto.getTotalAbsences());
        o.setPendingJustifications(dto.getPendingJustifications());
        return o;
    }
}