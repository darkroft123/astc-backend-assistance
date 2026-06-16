package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.GetDashboardPort;
import com.smms.assistance.application.in.GetJustificationStatusPort;
import com.smms.assistance.application.in.ListAbsencesPort;
import com.smms.assistance.application.in.ListAttendancePort;
import com.smms.assistance.application.in.ListJustificationsPort;

import com.smms.assistance.infrastructure.controller.graphql.dto.*;

import com.smms.assistance.infrastructure.security.AuthContext;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.UUID;

@GraphQLApi
@ApplicationScoped
@RolesAllowed("TEAM_MEMBER")
public class AssistanceQueryResolver {

    private static final Logger LOG =
            Logger.getLogger(AssistanceQueryResolver.class);

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

    @Inject
    com.smms.assistance.infrastructure.client.ProjectManagerClientWrapper pmClientWrapper;

    // =========================
    // GET MY PROJECTS
    // =========================
    @Query("GetMyProjects")
    public java.util.List<com.smms.assistance.infrastructure.client.ProjectOutput> getMyProjects() {
        logSecurity("GetMyProjects");
        return pmClientWrapper.getMyProjects();
    }

    // =========================
    // GET DASHBOARD
    // =========================
    @Query("GetDashboard")
    public DashboardOutput getDashboard(
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate) {

        logSecurity("GetDashboard");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        LocalDate from =
                fromDate != null
                        ? LocalDate.parse(fromDate)
                        : null;

        LocalDate to =
                toDate != null
                        ? LocalDate.parse(toDate)
                        : null;

        DashboardOutput output =
                DtoMapper.toDashboardOutput(
                        getDashboardPort.execute(userId, from, to));

        LOG.info("✅ DASHBOARD LOADED");

        return output;
    }

    // =========================
    // LIST ATTENDANCE
    // =========================
    @Query("ListAttendance")
    public AttendancePageOutput listAttendance(
            @Name("page") int page,
            @Name("size") int size,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("status") String status) {

        logSecurity("ListAttendance");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        return DtoMapper.toAttendancePage(
                listAttendancePort.execute(
                        userId,
                        page,
                        size,
                        projectId,
                        fromDate,
                        toDate,
                        status));
    }

    // =========================
    // LIST ABSENCES
    // =========================
    @Query("ListAbsences")
    public AbsencePageOutput listAbsences(
            @Name("page") int page,
            @Name("size") int size,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("type") String type,
            @Name("justified") Boolean justified) {

        logSecurity("ListAbsences");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        return DtoMapper.toAbsencePage(
                listAbsencesPort.execute(
                        userId,
                        page,
                        size,
                        projectId,
                        fromDate,
                        toDate,
                        type,
                        justified));
    }

    // =========================
    // LIST JUSTIFICATIONS
    // =========================
    @Query("ListJustifications")
    public JustificationPageOutput listJustifications(
            @Name("page") int page,
            @Name("size") int size,
            @Name("status") String status,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate) {

        logSecurity("ListJustifications");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        return DtoMapper.toJustificationPage(
                listJustificationsPort.execute(
                        userId,
                        page,
                        size,
                        status,
                        fromDate,
                        toDate));
    }

    // =========================
    // GET JUSTIFICATION STATUS
    // =========================
    @Query("GetJustificationStatus")
    public JustificationStatusOutput getJustificationStatus(
            @Name("justificationId") UUID justificationId) {

        logSecurity("GetJustificationStatus");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);
        LOG.info("📌 JUSTIFICATION ID: " + justificationId);

        return DtoMapper.toJustificationStatusOutput(
                getJustificationStatusPort.execute(
                        userId,
                        justificationId));
    }

    // =========================
    // SECURITY LOGGER
    // =========================
    private void logSecurity(String method) {

        LOG.info("====================================");
        LOG.info("🔥 ENTER: " + method);

        try {

            UUID userId = authContext.getUserId();

            LOG.info("🔐 JWT DETECTED");
            LOG.info("👤 USER ID: " + userId);

        } catch (Exception e) {

            LOG.error("❌ JWT NOT DETECTED");
            LOG.error(e.getMessage());
        }

        LOG.info("====================================");
    }
}