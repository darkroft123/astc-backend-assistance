package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.*;
import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;
import java.util.UUID;

@GraphQLApi
public class AssistanceMutationResolver {

    private static final UUID TEST_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Inject AuthContext authContext;
    @Inject RegisterAttendancePort registerAttendancePort;
    @Inject JustifyAbsencePort justifyAbsencePort;
    @Inject SaveJustificationPort saveJustificationPort;

    @Mutation("registerAttendance")
    //@RolesAllowed("TEAM_MEMBER")
    public AttendanceOutput registerAttendance(@Name("input") RegisterAttendanceInput input) {
        try {
            UUID userId = TEST_USER_ID;
            System.out.println("registerAttendance USER => " + userId);
            System.out.println("INPUT DATE => " + input.getDate());
            System.out.println("INPUT CHECKIN => " + input.getCheckIn());
            System.out.println("INPUT CHECKOUT => " + input.getCheckOut());
            System.out.println("INPUT PROJECTID => " + input.getProjectId());
            System.out.println("INPUT LATITUDE => " + input.getLatitude());
            System.out.println("INPUT LONGITUDE => " + input.getLongitude());
            System.out.println("INPUT PHOTO FILENAME => " + input.getPhotoFilename());
            System.out.println("INPUT PHOTO BASE64 NULL? => " + (input.getPhotoBase64() == null));
            System.out.println("ANTES DE EXECUTE registerAttendance");

            var result = registerAttendancePort.execute(userId, input);

            System.out.println("DESPUES DE EXECUTE registerAttendance => " + result);

            return DtoMapper.toAttendanceOutput(result);
        } catch (Exception e) {
            System.out.println("ERROR EN registerAttendance => " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Mutation("justifyAbsence")
    //@RolesAllowed("TEAM_MEMBER")
    public AbsenceOutput justifyAbsence(@Name("input") JustifyAbsenceInput input) {
        try {
            UUID userId = TEST_USER_ID;
            System.out.println("justifyAbsence USER => " + userId);
            System.out.println("INPUT => " + input);

            return DtoMapper.toAbsenceOutput(
                    justifyAbsencePort.execute(userId, input.getAbsenceId()));
        } catch (Exception e) {
            System.out.println("ERROR EN justifyAbsence => " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Mutation("saveJustification")
    //@RolesAllowed("TEAM_MEMBER")
    public JustificationOutput saveJustification(@Name("input") SaveJustificationInput input) {
        try {
            UUID userId = TEST_USER_ID;
            System.out.println("saveJustification USER => " + userId);
            System.out.println("INPUT => " + input);

            return DtoMapper.toJustificationOutput(
                    saveJustificationPort.execute(userId, input));
        } catch (Exception e) {
            System.out.println("ERROR EN saveJustification => " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}