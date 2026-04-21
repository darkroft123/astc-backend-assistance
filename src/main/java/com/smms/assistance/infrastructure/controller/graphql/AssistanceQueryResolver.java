package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.GetDashboardPort;
import com.smms.assistance.application.in.GetJustificationStatusPort;
import com.smms.assistance.application.in.ListAbsencesPort;
import com.smms.assistance.application.in.ListAttendancePort;
import com.smms.assistance.application.in.ListJustificationsPort;
import com.smms.assistance.infrastructure.controller.graphql.dto.AbsencePageOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.AttendancePageOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.DashboardOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.DtoMapper;
import com.smms.assistance.infrastructure.controller.graphql.dto.JustificationPageOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.JustificationStatusOutput;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import java.time.LocalDate;
import java.util.UUID;

@GraphQLApi
public class AssistanceQueryResolver {

    @Inject
    AuthContext authContext;
    @Inject
    GetDashboardPort getDashboardPort;
    @Inject
    ListAttendancePort listAttendancePort;
    @Inject
    ListAbsencesPort listAbsencesPort;
    @Inject
    ListJustificationsPort listJustificationsPort;
    @Inject
    GetJustificationStatusPort getJustificationStatusPort;

    @Query("GetDashboard")
    public DashboardOutput getDashboard(
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;
        return DtoMapper.toDashboardOutput(getDashboardPort.execute(userId, from, to));
    }

    @Query("ListAttendance")
    public AttendancePageOutput listAttendance(
            @Name("page") int page,
            @Name("size") int size,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("status") String status) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toAttendancePage(
                listAttendancePort.execute(userId, page, size, projectId, fromDate, toDate, status));
    }

    @Query("ListAbsences")
    public AbsencePageOutput listAbsences(
            @Name("page") int page,
            @Name("size") int size,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("type") String type,
            @Name("justified") Boolean justified) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toAbsencePage(
                listAbsencesPort.execute(userId, page, size, projectId, fromDate, toDate, type, justified));
    }

    @Query("ListJustifications")
    public JustificationPageOutput listJustifications(
            @Name("page") int page,
            @Name("size") int size,
            @Name("status") String status,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toJustificationPage(
                listJustificationsPort.execute(userId, page, size, status, fromDate, toDate));
    }

    @Query("GetJustificationStatus")
    public JustificationStatusOutput getJustificationStatus(
            @Name("justificationId") UUID justificationId) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toJustificationStatusOutput(
                getJustificationStatusPort.execute(userId, justificationId));
    }
}