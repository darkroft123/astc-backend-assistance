package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.JustifyAbsencePort;
import com.smms.assistance.application.in.RegisterAttendancePort;
import com.smms.assistance.application.in.SaveJustificationPort;
import com.smms.assistance.infrastructure.controller.graphql.dto.DtoMapper;
import com.smms.assistance.infrastructure.controller.graphql.dto.JustifyAbsenceInput;
import com.smms.assistance.infrastructure.controller.graphql.dto.JustifyAbsenceOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceInput;
import com.smms.assistance.infrastructure.controller.graphql.dto.RegisterAttendanceOutput;
import com.smms.assistance.infrastructure.controller.graphql.dto.SaveJustificationInput;
import com.smms.assistance.infrastructure.controller.graphql.dto.SaveJustificationOutput;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;

import java.util.UUID;

@GraphQLApi
public class AssistanceMutationResolver {

    @Inject
    AuthContext authContext;
    @Inject
    RegisterAttendancePort registerAttendancePort;
    @Inject
    JustifyAbsencePort justifyAbsencePort;
    @Inject
    SaveJustificationPort saveJustificationPort;

    @Mutation("RegisterAttendance")
    public RegisterAttendanceOutput registerAttendance(@Name("input") RegisterAttendanceInput input) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toRegisterAttendanceOutput(
                registerAttendancePort.execute(userId, input));
    }

    @Mutation("JustifyAbsence")
    public JustifyAbsenceOutput justifyAbsence(@Name("input") JustifyAbsenceInput input) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        var absence = justifyAbsencePort.execute(userId, input.getAbsenceId());
        return new JustifyAbsenceOutput(absence.getId(), true);
    }

    @Mutation("SaveJustification")
    public SaveJustificationOutput saveJustification(@Name("input") SaveJustificationInput input) {
        authContext.assertTeamMember();
        UUID userId = authContext.getUserId();
        return DtoMapper.toSaveJustificationOutput(
                saveJustificationPort.execute(userId, input));
    }
}