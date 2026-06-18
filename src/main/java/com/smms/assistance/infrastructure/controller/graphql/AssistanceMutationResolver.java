package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.JustifyAbsencePort;
import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.application.in.SaveJustificationPort;

import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.*;
import org.jboss.logging.Logger;
import com.smms.assistance.infrastructure.client.AlertsClient;


import java.util.UUID;

@GraphQLApi
@ApplicationScoped
@RolesAllowed({"TEAM_MEMBER", "PROJECT_MANAGER"})
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

    // =========================
    // REGISTER ATTENDANCE
    // =========================
    @Mutation("RegisterAttendance")
    @RolesAllowed("TEAM_MEMBER")
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
    // JUSTIFY ABSENCE
    // =========================
    @Mutation("JustifyAbsence")
    @RolesAllowed("TEAM_MEMBER")
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
    @Mutation("SaveJustification")
    @RolesAllowed("TEAM_MEMBER")
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

    // ==========================================
    // PROJECT MANAGER MUTATIONS
    // ==========================================

    @Mutation("ApproveJustificationPM")
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

        var result = approveJustificationPort.execute(justificationId, comment, reviewedBy);

        System.out.println("[Assistance PM API] [Mutation] ApproveJustificationPM completed -> justificationId: " + justificationId + ", new status: " + result.getStatus());

        return DtoMapper.toJustificationOutput(result);
    }

    @Mutation("RejectJustificationPM")
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

        var result = rejectJustificationPort.execute(justificationId, comment, reviewedBy);

        System.out.println("[Assistance PM API] [Mutation] RejectJustificationPM completed -> justificationId: " + justificationId + ", new status: " + result.getStatus());

        return DtoMapper.toJustificationOutput(result);
    }
}