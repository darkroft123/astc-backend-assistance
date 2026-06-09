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
@RolesAllowed({"TEAM_MEMBER"})
public class AssistanceMutationResolver {

    private static final Logger LOG =
            Logger.getLogger(AssistanceMutationResolver.class);

    @Inject
    AlertsClient alertsClient;

    @Inject
    AuthContext authContext;

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
    public RegisterAttendanceOutput registerAttendance(
            @Name("input") RegisterAttendanceInput input) {

        logSecurity("RegisterAttendance");

        UUID userId = authContext.getUserId();

        LOG.infof("🚀 RegisterAttendance START -> userId=%s input=%s", userId, input);

        try {
            LOG.info("📦 Calling RegisterAttendancePort...");

            var result = registerAttendancePort.execute(userId, input);

            if (result != null) {
                alertsClient.sendAlert(result);
            }

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
}