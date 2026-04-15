package com.devastc.asistencia.infrastructure.mapper;

import com.devastc.asistencia.domain.model.Absence;
import com.devastc.asistencia.infrastructure.persistence.entity.AbsenceEntity;
import com.devastc.asistencia.domain.enumtype.AbsenceType;

public class AbsenceMapper {
    public static Absence toDomain(AbsenceEntity e) {
        Absence d = new Absence();
        d.setId(e.getId());
        d.setUserId(e.getUserId());
        d.setProjectId(e.getProjectId());
        d.setDate(e.getDate());
        d.setType(AbsenceType.valueOf(e.getType()));
        d.setJustified(e.isJustified());
        return d;
    }

    public static AbsenceEntity toEntity(Absence d) {
        AbsenceEntity e = new AbsenceEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setProjectId(d.getProjectId());
        e.setDate(d.getDate());
        e.setType(d.getType().name());
        e.setJustified(d.isJustified());
        return e;
    }
}
