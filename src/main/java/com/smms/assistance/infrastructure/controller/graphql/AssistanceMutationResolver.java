package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.*;
import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;
import java.util.UUID;

@GraphQLApi
public class AssistanceMutationResolver {

    @Inject AuthContext authContext;
    @Inject RegisterAttendancePort registerAttendancePort;
    @Inject JustifyAbsencePort justifyAbsencePort;
    @Inject SaveJustificationPort saveJustificationPort;

    @Mutation("registerAttendance")
    @RolesAllowed("TEAM_MEMBER")
    public AttendanceOutput registerAttendance(@Name("input") RegisterAttendanceInput input) {
        UUID userId = authContext.getUserId();
        return DtoMapper.toAttendanceOutput(
                registerAttendancePort.execute(userId, input));
    }

    @Mutation("justifyAbsence")
    @RolesAllowed("TEAM_MEMBER")
    public AbsenceOutput justifyAbsence(@Name("input") JustifyAbsenceInput input) {
        UUID userId = authContext.getUserId();
        return DtoMapper.toAbsenceOutput(
                justifyAbsencePort.execute(userId, input.getAbsenceId()));
    }

    @Mutation("saveJustification")
    @RolesAllowed("TEAM_MEMBER")
    public JustificationOutput saveJustification(@Name("input") SaveJustificationInput input) {
        UUID userId = authContext.getUserId();
        return DtoMapper.toJustificationOutput(
                saveJustificationPort.execute(userId, input));
    }
}
