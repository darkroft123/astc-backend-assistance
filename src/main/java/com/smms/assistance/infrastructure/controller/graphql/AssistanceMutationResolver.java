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

import java.util.UUID;

@GraphQLApi
@ApplicationScoped
@RolesAllowed({"TEAM_MEMBER"})
public class AssistanceMutationResolver {

    private static final Logger LOG =
            Logger.getLogger(AssistanceMutationResolver.class);

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

        authContext.assertTeamMember();

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        RegisterAttendanceOutput output =
                DtoMapper.toRegisterAttendanceOutput(
                        registerAttendancePort.execute(userId, input));

        LOG.info("✅ ATTENDANCE REGISTERED");

        return output;
    }

    // =========================
    // JUSTIFY ABSENCE
    // =========================
    @Mutation("JustifyAbsence")
    public JustifyAbsenceOutput justifyAbsence(
            @Name("input") JustifyAbsenceInput input) {

        logSecurity("JustifyAbsence");

        authContext.assertTeamMember();

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);
        LOG.info("📌 ABSENCE ID: " + input.getAbsenceId());

        var absence =
                justifyAbsencePort.execute(
                        userId,
                        input.getAbsenceId());

        LOG.info("✅ ABSENCE JUSTIFIED");

        return new JustifyAbsenceOutput(
                absence.getId(),
                true);
    }

    // =========================
    // SAVE JUSTIFICATION
    // =========================
    @Mutation("SaveJustification")
    public SaveJustificationOutput saveJustification(
            @Name("input") SaveJustificationInput input) {

        logSecurity("SaveJustification");

        authContext.assertTeamMember();

        UUID userId = authContext.getUserId();

        LOG.info("📌 USER ID: " + userId);

        SaveJustificationOutput output =
                DtoMapper.toSaveJustificationOutput(
                        saveJustificationPort.execute(userId, input));

        LOG.info("✅ JUSTIFICATION SAVED");

        return output;
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