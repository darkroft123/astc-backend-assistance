package com.devastc.asistencia.infrastructure.mapper;

import com.devastc.asistencia.domain.model.Attendance;
import com.devastc.asistencia.infrastructure.persistence.entity.AttendanceEntity;
import com.devastc.asistencia.domain.enumtype.AttendanceStatus;

public class AttendanceMapper {
    public static Attendance toDomain(AttendanceEntity e) {
        Attendance d = new Attendance();
        d.setId(e.getId());
        d.setUserId(e.getUserId());
        d.setProjectId(e.getProjectId());
        d.setDate(e.getDate());
        d.setCheckIn(e.getCheckIn());
        d.setCheckOut(e.getCheckOut());
        d.setLatitude(e.getLatitude());
        d.setLongitude(e.getLongitude());
        d.setPhotoUrl(e.getPhotoUrl());
        d.setStatus(AttendanceStatus.valueOf(e.getStatus()));
        return d;
    }

    public static AttendanceEntity toEntity(Attendance d) {
        AttendanceEntity e = new AttendanceEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setProjectId(d.getProjectId());
        e.setDate(d.getDate());
        e.setCheckIn(d.getCheckIn());
        e.setCheckOut(d.getCheckOut());
        e.setLatitude(d.getLatitude());
        e.setLongitude(d.getLongitude());
        e.setPhotoUrl(d.getPhotoUrl());
        e.setStatus(d.getStatus().name());
        return e;
    }
}
