package com.devastc.asistencia.infrastructure.mapper;

import com.devastc.asistencia.domain.model.Justification;
import com.devastc.asistencia.infrastructure.persistence.entity.JustificationEntity;
import com.devastc.asistencia.domain.enumtype.JustificationStatus;

public class JustificationMapper {
    public static Justification toDomain(JustificationEntity e) {
        Justification d = new Justification();
        d.setId(e.getId());
        d.setAbsenceId(e.getAbsenceId());
        d.setUserId(e.getUserId());
        d.setDescription(e.getDescription());
        d.setDocumentUrl(e.getDocumentUrl());
        d.setComment(e.getComment());
        d.setSubmittedAt(e.getSubmittedAt());
        d.setReviewedAt(e.getReviewedAt());
        d.setReviewedBy(e.getReviewedBy());
        d.setStatus(JustificationStatus.valueOf(e.getStatus()));
        return d;
    }

    public static JustificationEntity toEntity(Justification d) {
        JustificationEntity e = new JustificationEntity();
        e.setId(d.getId());
        e.setAbsenceId(d.getAbsenceId());
        e.setUserId(d.getUserId());
        e.setDescription(d.getDescription());
        e.setDocumentUrl(d.getDocumentUrl());
        e.setComment(d.getComment());
        e.setSubmittedAt(d.getSubmittedAt());
        e.setReviewedAt(d.getReviewedAt());
        e.setReviewedBy(d.getReviewedBy());
        e.setStatus(d.getStatus().name());
        return e;
    }
}
