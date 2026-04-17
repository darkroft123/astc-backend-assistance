package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.*;
import com.smms.assistance.domain.model.*;
import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;
import java.time.LocalDate;
import java.util.UUID;

@GraphQLApi
public class AssistanceQueryResolver {

    @Inject AuthContext authContext;
    @Inject GetDashboardPort getDashboardPort;
    @Inject ListAttendancePort listAttendancePort;
    @Inject ListAbsencesPort listAbsencesPort;
    @Inject ListJustificationsPort listJustificationsPort;
    @Inject GetJustificationStatusPort getJustificationStatusPort;

    @Query("getDashboard")
    
    public DashboardOutput getDashboard() {
        UUID userId = authContext.getUserId();
        DashboardSummary summary = getDashboardPort.execute(userId);
        return DtoMapper.toDashboardOutput(summary);
    }

    @Query("listAttendance")
    @RolesAllowed("TEAM_MEMBER")
    public AttendancePageOutput listAttendance(@Name("filter") AttendanceFilterInput filter) {
        UUID userId = authContext.getUserId();
        AttendanceStatus status = filter != null && filter.getStatus() != null
                ? AttendanceStatus.valueOf(filter.getStatus()) : null;
        LocalDate from = filter != null && filter.getFrom() != null ? LocalDate.parse(filter.getFrom()) : null;
        LocalDate to = filter != null && filter.getTo() != null ? LocalDate.parse(filter.getTo()) : null;
        String search = filter != null ? filter.getSearch() : null;
        int page = filter != null ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() > 0 ? filter.getSize() : 20;

        Page<com.smms.assistance.domain.entity.AttendanceEntity> result =
                listAttendancePort.execute(userId, from, to, status, search, page, size);
        return DtoMapper.toAttendancePage(result);
    }

    @Query("listAbsences")
    @RolesAllowed("TEAM_MEMBER")
    public java.util.List<AbsenceOutput> listAbsences() {
        UUID userId = authContext.getUserId();
        return listAbsencesPort.execute(userId).stream()
                .map(DtoMapper::toAbsenceOutput)
                .toList();
    }

    @Query("listJustifications")
    @RolesAllowed("TEAM_MEMBER")
    public JustificationPageOutput listJustifications(@Name("filter") JustificationFilterInput filter) {
        UUID userId = authContext.getUserId();
        JustificationStatus status = filter != null && filter.getStatus() != null
                ? JustificationStatus.valueOf(filter.getStatus()) : null;
        LocalDate from = filter != null && filter.getFrom() != null ? LocalDate.parse(filter.getFrom()) : null;
        LocalDate to = filter != null && filter.getTo() != null ? LocalDate.parse(filter.getTo()) : null;
        int page = filter != null ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() > 0 ? filter.getSize() : 20;

        Page<com.smms.assistance.domain.entity.JustificationEntity> result =
                listJustificationsPort.execute(userId, status, from, to, page, size);
        return DtoMapper.toJustificationPage(result);
    }

    @Query("getJustificationStatus")
    @RolesAllowed("TEAM_MEMBER")
    public JustificationStatusOutput getJustificationStatus(@Name("justificationId") UUID justificationId) {
        UUID userId = authContext.getUserId();
        return DtoMapper.toJustificationStatusOutput(
                getJustificationStatusPort.execute(userId, justificationId));
    }
}
