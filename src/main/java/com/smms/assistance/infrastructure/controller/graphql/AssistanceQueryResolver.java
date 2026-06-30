package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.GetDashboardPort;
import com.smms.assistance.application.in.GetJustificationStatusPort;
import com.smms.assistance.application.in.ListAbsencesPort;
import com.smms.assistance.application.in.ListAttendancePort;
import com.smms.assistance.application.in.ListJustificationsPort;

import com.smms.assistance.infrastructure.controller.graphql.dto.*;

import com.smms.assistance.infrastructure.client.ProjectOutput;
import com.smms.assistance.infrastructure.security.AuthContext;
import com.smms.assistance.shared.util.TimezoneService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import org.jboss.logging.Logger;

import io.quarkus.panache.common.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GraphQLApi
@ApplicationScoped
@RolesAllowed({"TEAM_MEMBER", "PROJECT_MANAGER"})
public class AssistanceQueryResolver {

    private static final Logger LOG =
            Logger.getLogger(AssistanceQueryResolver.class);

    @Inject
    AuthContext authContext;

    @Inject
    com.smms.assistance.infrastructure.repository.AttendanceRepository attendanceRepository;

    @Inject
    com.smms.assistance.infrastructure.repository.AbsenceRepository absenceRepository;

    @Inject
    com.smms.assistance.infrastructure.repository.JustificationRepository justificationRepository;

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

    @Inject
    com.smms.assistance.infrastructure.storage.S3StorageAdapter s3StorageAdapter;

    // =========================
    // GET MY PROJECTS
    // =========================
    @Query("GetMyProjects")
    @RolesAllowed("TEAM_MEMBER")
    public java.util.List<com.smms.assistance.infrastructure.client.ProjectOutput> getMyProjects() {
        logSecurity("GetMyProjects");
        return pmClientWrapper.getMyProjects();
    }

    // =========================
    // GET DASHBOARD
    // =========================
    @Query("GetDashboard")
    @RolesAllowed("TEAM_MEMBER")
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
    @RolesAllowed("TEAM_MEMBER")
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

        AttendancePageOutput output = DtoMapper.toAttendancePage(
                listAttendancePort.execute(
                        userId,
                        page,
                        size,
                        projectId,
                        fromDate,
                        toDate,
                        status));

        if (output.getItems() != null) {
            for (AttendanceOutput item : output.getItems()) {
                if (item.getPhotoUrl() != null && !item.getPhotoUrl().isBlank()) {
                    item.setPhotoUrl(s3StorageAdapter.getPublicUrl(item.getPhotoUrl()));
                }
            }
        }

        return output;
    }

    // =========================
    // GET TODAY'S ATTENDANCE
    // =========================
    @Query("GetTodayAttendance")
    @RolesAllowed("TEAM_MEMBER")
    public AttendanceOutput getTodayAttendance(@Name("projectId") UUID projectId) {
        UUID userId = authContext.getUserId();
        String timezone = resolveProjectTimezone(userId, projectId);
        java.time.LocalDate today = TimezoneService.todayAtZone(timezone);

        var existing = attendanceRepository
                .find("userId = ?1 and projectId = ?2 and date = ?3", userId, projectId, today)
                .firstResultOptional();

        if (existing.isEmpty()) return null;

        AttendanceOutput output = DtoMapper.toAttendanceOutput(existing.get());
        if (output.getPhotoUrl() != null && !output.getPhotoUrl().isBlank()) {
            output.setPhotoUrl(s3StorageAdapter.getPublicUrl(output.getPhotoUrl()));
        }
        return output;
    }

    // =========================
    // LIST ABSENCES
    // =========================
    @Query("ListAbsences")
    @RolesAllowed("TEAM_MEMBER")
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
    @RolesAllowed("TEAM_MEMBER")
    public JustificationPageOutput listJustifications(
            @Name("page") int page,
            @Name("size") int size,
            @Name("status") String status,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate) {

        logSecurity("ListJustifications");

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        JustificationPageOutput output = DtoMapper.toJustificationPage(
                listJustificationsPort.execute(
                        userId,
                        page,
                        size,
                        status,
                        fromDate,
                        toDate));

        if (output.getItems() != null) {
            for (JustificationOutput item : output.getItems()) {
                if (item.getDocumentUrl() != null && !item.getDocumentUrl().isBlank()) {
                    item.setDocumentUrl(s3StorageAdapter.getPublicUrl(item.getDocumentUrl()));
                }
            }
        }

        return output;
    }

    // =========================
    // GET JUSTIFICATION STATUS
    // =========================
    @Query("GetJustificationStatus")
    @RolesAllowed("TEAM_MEMBER")
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

    // ==========================================
    // PROJECT MANAGER ENDPOINTS
    // ==========================================

    @Query("ListTeamAttendancePM")
    @RolesAllowed("PROJECT_MANAGER")
    public AttendancePageOutput listTeamAttendancePM(
            @Name("userIds") java.util.List<UUID> userIds,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("status") String status,
            @Name("page") int page,
            @Name("size") int size
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Query] ListTeamAttendancePM resolver execution flow starting. Initiated by PM: " + currentPmId 
                + " -> userIds count: " + (userIds != null ? userIds.size() : 0) 
                + ", projectId: " + projectId 
                + ", fromDate: " + fromDate 
                + ", toDate: " + toDate 
                + ", status: " + status 
                + ", page: " + page 
                + ", size: " + size);

        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;

        java.util.List<com.smms.assistance.domain.entity.AttendanceEntity> items = 
                attendanceRepository.findByUsers(userIds, projectId, from, to, status, page, size);
        long total = attendanceRepository.countByUsers(userIds, projectId, from, to, status);

        long photosCount = items.stream().filter(a -> a.getPhotoUrl() != null && !a.getPhotoUrl().isBlank()).count();
        System.out.println("[Assistance PM API] [Query] ListTeamAttendancePM matched " + items.size() + " records out of " + total + " total. Photos: " + photosCount);

        AttendancePageOutput output = DtoMapper.toAttendancePage(
                new com.smms.assistance.domain.model.Page<>(items, total, page, size)
        );

        if (output.getItems() != null) {
            for (AttendanceOutput item : output.getItems()) {
                if (item.getPhotoUrl() != null && !item.getPhotoUrl().isBlank()) {
                    item.setPhotoUrl(s3StorageAdapter.getPublicUrl(item.getPhotoUrl()));
                }
            }
        }

        System.out.println("[Assistance PM API] [Query] ListTeamAttendancePM resolved successfully and returning response page");

        return output;
    }

    @Query("ListTeamJustificationsPM")
    @RolesAllowed("PROJECT_MANAGER")
    public JustificationPageOutput listTeamJustificationsPM(
            @Name("userIds") java.util.List<UUID> userIds,
            @Name("status") String status,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate,
            @Name("page") int page,
            @Name("size") int size
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Query] ListTeamJustificationsPM initiated by PM: " + currentPmId 
                + " -> userIds count: " + (userIds != null ? userIds.size() : 0) 
                + ", status: " + status 
                + ", fromDate: " + fromDate 
                + ", toDate: " + toDate 
                + ", page: " + page 
                + ", size: " + size);

        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;

        java.util.List<com.smms.assistance.domain.entity.JustificationEntity> items = 
                justificationRepository.findByUsers(userIds, status, from, to, page, size);
        long total = justificationRepository.countByUsers(userIds, status, from, to);

        System.out.println("[Assistance PM API] [Query] ListTeamJustificationsPM matched " + items.size() + " justifications out of " + total + " total.");

        JustificationPageOutput output = DtoMapper.toJustificationPage(
                new com.smms.assistance.domain.model.Page<>(items, total, page, size)
        );

        if (output.getItems() != null) {
            for (JustificationOutput item : output.getItems()) {
                if (item.getDocumentUrl() != null && !item.getDocumentUrl().isBlank()) {
                    item.setDocumentUrl(s3StorageAdapter.getPublicUrl(item.getDocumentUrl()));
                }
            }
        }

        return output;
    }

    @Query("GetJustificationDetailPM")
    public JustificationOutput getJustificationDetailPM(@Name("id") UUID id) {
        UUID currentUserId = authContext.getUserId();
        String currentRole = authContext.getRole();
        System.out.println("[Assistance PM API] [Query] GetJustificationDetailPM initiated by user: " + currentUserId + " (role: " + currentRole + ") -> justificationId: " + id);

        com.smms.assistance.domain.entity.JustificationEntity justification = justificationRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Justification not found"));

        if ("TEAM_MEMBER".equals(currentRole) && !justification.getUserId().equals(currentUserId)) {
            throw new SecurityException("Access denied: justification does not belong to you");
        }

        System.out.println("[Assistance PM API] [Query] GetJustificationDetailPM found justification. Status: " + justification.getStatus());

        JustificationOutput output = DtoMapper.toJustificationOutput(justification);
        if (output.getDocumentUrl() != null && !output.getDocumentUrl().isBlank()) {
            output.setDocumentUrl(s3StorageAdapter.getPublicUrl(output.getDocumentUrl()));
        }

        com.smms.assistance.domain.entity.AbsenceEntity absence = absenceRepository.findById(justification.getAbsenceId());
        if (absence != null) {
            output.setAbsenceDate(absence.getDate() != null ? absence.getDate().toString() : null);
            output.setAbsenceType(absence.getType());
        }

        System.out.println("[Assistance PM API] [Query] GetJustificationDetailPM enriched with absence info -> absenceDate: " + output.getAbsenceDate() + ", absenceType: " + output.getAbsenceType());

        var historyEntities = com.smms.assistance.domain.entity.JustificationHistoryEntity
                .find("justificationId = ?1", Sort.by("changedAt").ascending(), id)
                .<com.smms.assistance.domain.entity.JustificationHistoryEntity>list();

        var historyOutputs = historyEntities.stream()
                .map(h -> JustificationHistoryOutput.builder()
                        .id(h.getId())
                        .justificationId(h.getJustificationId())
                        .previousStatus(h.getPreviousStatus())
                        .newStatus(h.getNewStatus())
                        .comment(h.getComment())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt() != null ? DtoMapper.utcToLima(h.getChangedAt()) : null)
                        .build())
                .collect(Collectors.toList());

        output.setHistory(historyOutputs);

        return output;
    }

    @Query("GetDashboardPMInternal")
    @RolesAllowed("PROJECT_MANAGER")
    public DashboardOutput getDashboardPMInternal(
            @Name("userIds") java.util.List<UUID> userIds,
            @Name("projectId") UUID projectId,
            @Name("fromDate") String fromDate,
            @Name("toDate") String toDate
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Query] GetDashboardPMInternal initiated by PM: " + currentPmId 
                + " -> userIds count: " + (userIds != null ? userIds.size() : 0) 
                + ", projectId: " + projectId 
                + ", fromDate: " + fromDate 
                + ", toDate: " + toDate);

        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;

        int totalAttendances = (int) attendanceRepository.countByUsers(userIds, projectId, from, to, null);
        int totalAbsences = (int) absenceRepository.countByUsers(userIds, projectId, from, to, null, null);
        int pendingJustifications = (int) justificationRepository.countByUsers(userIds, "SUBMITTED", from, to);

        System.out.println("[Assistance PM API] [Query] GetDashboardPMInternal summary -> Attendances: " + totalAttendances 
                + ", Absences: " + totalAbsences 
                + ", Pending Justifications: " + pendingJustifications);

        return DashboardOutput.builder()
                .totalAttendances(totalAttendances)
                .totalAbsences(totalAbsences)
                .pendingJustifications(pendingJustifications)
                .build();
    }

    private String resolveProjectTimezone(UUID userId, UUID projectId) {
        try {
            java.util.List<ProjectOutput> projects = pmClientWrapper.getMyProjects();
            if (projects != null) {
                for (ProjectOutput p : projects) {
                    if (p.id != null && p.id.equals(projectId) && p.getTimezone() != null
                            && TimezoneService.isValidTimezone(p.getTimezone())) {
                        return p.getTimezone();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[Query] Could not resolve project timezone, using default");
        }
        return TimezoneService.defaultTimezone();
    }
}