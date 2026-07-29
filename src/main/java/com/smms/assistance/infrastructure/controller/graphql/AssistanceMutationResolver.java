package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.JustifyAbsencePort;
import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.application.in.SaveJustificationPort;
import com.smms.assistance.application.in.EditJustificationPort;
import com.smms.assistance.application.in.DeleteJustificationPort;
import com.smms.assistance.application.in.SubmitJustificationPort;
import com.smms.assistance.application.in.CheckOutPort;
import com.smms.assistance.application.usecase.RequestObservationPMUseCase;

import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.*;
import org.jboss.logging.Logger;
import io.smallrye.common.annotation.Blocking;
import com.smms.assistance.infrastructure.client.AlertsClient;


import java.util.UUID;

@GraphQLApi
@ApplicationScoped
@RolesAllowed({"TEAM_MEMBER", "PROJECT_MANAGER", "ADMIN"})
@Blocking
public class AssistanceMutationResolver {

    private static final Logger LOG =
            Logger.getLogger(AssistanceMutationResolver.class);

    @Inject
    AlertsClient alertsClient;

    @Inject
    AuthContext authContext;

    @Inject
    com.smms.assistance.application.in.ApproveJustificationPort approveJustificationPort;

    @Inject
    com.smms.assistance.application.in.RejectJustificationPort rejectJustificationPort;

    @Inject
    RegisterAttendancePort registerAttendancePort;

    @Inject
    JustifyAbsencePort justifyAbsencePort;

    @Inject
    SaveJustificationPort saveJustificationPort;

    @Inject
    EditJustificationPort editJustificationPort;

    @Inject
    DeleteJustificationPort deleteJustificationPort;

    @Inject
    SubmitJustificationPort submitJustificationPort;

    @Inject
    RequestObservationPMUseCase requestObservationPMUseCase;

    @Inject
    CheckOutPort checkOutPort;

    @Inject
    com.smms.assistance.infrastructure.client.ProjectManagerClientWrapper pmClientWrapper;

    @Inject
    com.smms.assistance.infrastructure.repository.JustificationRepository justificationRepository;

    @Inject
    com.smms.assistance.infrastructure.repository.AbsenceRepository absenceRepository;

    // =========================
    // REGISTER ATTENDANCE
    // =========================
    @Mutation("registerAttendance")
    @RolesAllowed({"TEAM_MEMBER", "PROJECT_MANAGER", "ADMIN"})
public RegisterAttendanceOutput registerAttendance(
        @Name("input") RegisterAttendanceInput input) {

    logSecurity("RegisterAttendance");

    UUID userId = authContext.getUserId();

    LOG.infof("🚀 RegisterAttendance START -> userId=%s input=%s", userId, input);

    // 👇 AQUÍ LO AGREGAS
    LOG.info("INPUT RECEIVED: " + input);
    LOG.info("PROJECT ID: " + input.getProjectId());
    LOG.info("LAT: " + input.getLatitude());
    LOG.info("LNG: " + input.getLongitude());
    LOG.info("PHOTO SIZE: " + (input.getPhotoUrl() != null ? input.getPhotoUrl().length() : 0));

    try {
        LOG.info("📦 Calling RegisterAttendancePort...");

        var result = registerAttendancePort.execute(userId, input);

        LOG.info("📤 Port executed successfully -> checking S3/MinIO upload...");

        RegisterAttendanceOutput output =
                DtoMapper.toRegisterAttendanceOutput(result);

        LOG.infof("🏁 RegisterAttendance SUCCESS -> userId=%s", userId);

        return output;

    } catch (Exception e) {
        LOG.error("❌ RegisterAttendance FAILED", e);
        throw e;
    }
}

    // =========================
    // CHECK-OUT (EXIT REGISTRATION)
    // =========================
    @Mutation("checkOut")
    @RolesAllowed({"TEAM_MEMBER", "PROJECT_MANAGER", "ADMIN"})
    public RegisterAttendanceOutput checkOut(@Name("projectId") UUID projectId) {
        UUID userId = authContext.getUserId();
        LOG.infof("🚀 CheckOut START -> userId=%s projectId=%s", userId, projectId);

        var result = checkOutPort.execute(userId, projectId);
        return DtoMapper.toRegisterAttendanceOutput(result);
    }

    // =========================
    // JUSTIFY ABSENCE
    // =========================
    @Mutation("justifyAbsence")
    public JustifyAbsenceOutput justifyAbsence(
            @Name("input") JustifyAbsenceInput input) {

        logSecurity("JustifyAbsence");

        UUID userId = authContext.getUserId();

        LOG.infof("🚀 JustifyAbsence START -> userId=%s absenceId=%s",
                userId, input.getAbsenceId());

        try {
            var absence =
                    justifyAbsencePort.execute(userId, input.getAbsenceId());

            LOG.info("☁️ Absence processed (check S3 if evidence attached)");

            LOG.info("✅ ABSENCE JUSTIFIED SUCCESS");

            return new JustifyAbsenceOutput(
                    absence.getId(),
                    true);

        } catch (Exception e) {
            LOG.error("❌ JustifyAbsence FAILED", e);
            throw e;
        }
    }

    // =========================
    // SAVE JUSTIFICATION
    // =========================
    @Mutation("saveJustification")
    public SaveJustificationOutput saveJustification(
            @Name("input") SaveJustificationInput input) {

        logSecurity("SaveJustification");

        UUID userId = authContext.getUserId();

        LOG.infof("🚀 SaveJustification START -> userId=%s", userId);

        try {
            var result =
                    saveJustificationPort.execute(userId, input);

            SaveJustificationOutput output =
                    DtoMapper.toSaveJustificationOutput(result);

            LOG.info("📦 Justification saved (check if file uploaded to MinIO)");

            LOG.info("✅ SAVE JUSTIFICATION SUCCESS");

            return output;

        } catch (Exception e) {
            LOG.error("❌ SaveJustification FAILED", e);
            throw e;
        }
    }

    // =========================
    // SECURITY LOGGER
    // =========================
    private void logSecurity(String method) {

        LOG.info("====================================");
        LOG.info("🔥 ENTER METHOD: " + method);

        try {
            UUID userId = authContext.getUserId();

            LOG.info("🔐 JWT VALID");
            LOG.infof("👤 USER ID: %s", userId);

        } catch (Exception e) {
            LOG.error("❌ JWT NOT FOUND OR INVALID");
            LOG.error(e.getMessage());
        }

        LOG.info("====================================");
    }

    // =========================
    // EDIT JUSTIFICATION
    // =========================
    @Mutation("editJustification")
    public SaveJustificationOutput editJustification(
            @Name("id") UUID id,
            @Name("input") SaveJustificationInput input) {

        logSecurity("EditJustification");

        UUID userId = authContext.getUserId();

        LOG.infof("🚀 EditJustification START -> userId=%s justificationId=%s", userId, id);

        try {
            var result = editJustificationPort.execute(userId, id, input);

            SaveJustificationOutput output =
                    DtoMapper.toSaveJustificationOutput(result);

            LOG.info("✅ EDIT JUSTIFICATION SUCCESS");

            return output;

        } catch (Exception e) {
            LOG.error("❌ EditJustification FAILED", e);
            throw e;
        }
    }

    // =========================
    // DELETE JUSTIFICATION
    // =========================
    @Mutation("deleteJustification")
    public Boolean deleteJustification(@Name("id") UUID id) {

        logSecurity("DeleteJustification");

        UUID userId = authContext.getUserId();

        LOG.infof("🚀 DeleteJustification START -> userId=%s justificationId=%s", userId, id);

        try {
            deleteJustificationPort.execute(userId, id);

            LOG.info("✅ DELETE JUSTIFICATION SUCCESS");

            return true;

        } catch (Exception e) {
            LOG.error("❌ DeleteJustification FAILED", e);
            throw e;
        }
    }

    // =========================
    // SUBMIT JUSTIFICATION (PENDING/OBSERVATION → SUBMITTED)
    // =========================
    @Mutation("submitJustification")
    public SaveJustificationOutput submitJustification(@Name("id") UUID id) {
        UUID userId = authContext.getUserId();
        LOG.infof("🚀 SubmitJustification START -> userId=%s justificationId=%s", userId, id);

        try {
            var result = submitJustificationPort.execute(userId, id);
            SaveJustificationOutput output = DtoMapper.toSaveJustificationOutput(result);
            LOG.info("✅ SUBMIT JUSTIFICATION SUCCESS");
            return output;
        } catch (Exception e) {
            LOG.error("❌ SubmitJustification FAILED", e);
            throw e;
        }
    }

    // =========================
    // REQUEST OBSERVATION PM (SUBMITTED → OBSERVATION)
    // =========================
    @Mutation("requestObservationPM")
    @RolesAllowed("PROJECT_MANAGER")
    public JustificationOutput requestObservationPM(
            @Name("justificationId") UUID justificationId,
            @Name("comment") String comment,
            @Name("reviewedBy") UUID reviewedBy
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Mutation] RequestObservationPM by PM: " + currentPmId
                + " -> justificationId: " + justificationId);

        validatePmOwnershipForJustification(justificationId);

        var result = requestObservationPMUseCase.execute(justificationId, comment, reviewedBy);

        return DtoMapper.toJustificationOutput(result);
    }

    // ==========================================
    // PROJECT MANAGER MUTATIONS
    // ==========================================

    @Mutation("approveJustificationPM")
    @RolesAllowed("PROJECT_MANAGER")
    public JustificationOutput approveJustificationPM(
            @Name("justificationId") UUID justificationId,
            @Name("comment") String comment,
            @Name("reviewedBy") UUID reviewedBy
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Mutation] ApproveJustificationPM initiated by PM: " + currentPmId 
                + " -> justificationId: " + justificationId 
                + ", comment: " + comment 
                + ", reviewedBy: " + reviewedBy);

        validatePmOwnershipForJustification(justificationId);

        var result = approveJustificationPort.execute(justificationId, comment, reviewedBy);

        System.out.println("[Assistance PM API] [Mutation] ApproveJustificationPM completed -> justificationId: " + justificationId + ", new status: " + result.getStatus());

        return DtoMapper.toJustificationOutput(result);
    }

    @Mutation("rejectJustificationPM")
    @RolesAllowed("PROJECT_MANAGER")
    public JustificationOutput rejectJustificationPM(
            @Name("justificationId") UUID justificationId,
            @Name("comment") String comment,
            @Name("reviewedBy") UUID reviewedBy
    ) {
        UUID currentPmId = authContext.getUserId();
        System.out.println("[Assistance PM API] [Mutation] RejectJustificationPM initiated by PM: " + currentPmId 
                + " -> justificationId: " + justificationId 
                + ", comment: " + comment 
                + ", reviewedBy: " + reviewedBy);

        validatePmOwnershipForJustification(justificationId);

        var result = rejectJustificationPort.execute(justificationId, comment, reviewedBy);

        System.out.println("[Assistance PM API] [Mutation] RejectJustificationPM completed -> justificationId: " + justificationId + ", new status: " + result.getStatus());

        return DtoMapper.toJustificationOutput(result);
    }

    // ==========================================
    // SECURITY VALIDATION (IDOR PATCH + LOGS)
    // ==========================================
    private void validatePmOwnershipForJustification(UUID justificationId) {
        String role = authContext.getRole();
        if ("ADMIN".equals(role)) {
            System.out.println("🛡️ [Security] ADMIN bypass granted for justificationId: " + justificationId);
            return;
        }
        if ("PROJECT_MANAGER".equals(role)) {
            com.smms.assistance.domain.entity.JustificationEntity justification = justificationRepository.findById(justificationId);
            if (justification == null) throw new SecurityException("Access Denied: Justification not found");
            
            com.smms.assistance.domain.entity.AbsenceEntity absence = absenceRepository.findById(justification.getAbsenceId());
            if (absence == null || absence.getProjectId() == null) {
                throw new SecurityException("Access Denied: Absence or Project not found");
            }
            
            UUID projectId = absence.getProjectId();
            try {
                java.util.List<com.smms.assistance.infrastructure.client.ProjectOutput> projects = pmClientWrapper.getMyProjects();
                if (projects == null || projects.stream().noneMatch(p -> p.id != null && p.id.equals(projectId))) {
                    System.out.println("🚨 [Security IDOR Blocked] PM " + authContext.getUserId() + " attempted to access unauthorized justificationId: " + justificationId);
                    throw new SecurityException("Access Denied: You do not manage the project for this justification");
                }
                System.out.println("✅ [Security] PM Ownership validated for justification: " + justificationId);
            } catch (Exception e) {
                System.out.println("🚨 [Security Error] " + e.getMessage());
                throw new SecurityException("Access Denied: Could not verify project ownership");
            }
        } else {
            System.out.println("🚨 [Security Blocked] Unauthorized role " + role + " attempted to access justification: " + justificationId);
            throw new SecurityException("Access Denied: Insufficient role");
        }
    }
}