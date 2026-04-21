package com.smms.assistance.infrastructure.controller.graphql;

import com.smms.assistance.application.in.*;
import com.smms.assistance.domain.model.*;
import com.smms.assistance.infrastructure.controller.graphql.dto.*;
import com.smms.assistance.infrastructure.security.AuthContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;
import java.time.LocalDate;
import java.util.UUID;

@GraphQLApi
public class AssistanceQueryResolver {

    private static final UUID TEST_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Inject AuthContext authContext;
    @Inject GetDashboardPort getDashboardPort;
    @Inject ListAttendancePort listAttendancePort;
    @Inject ListAbsencesPort listAbsencesPort;
    @Inject ListJustificationsPort listJustificationsPort;
    @Inject GetJustificationStatusPort getJustificationStatusPort;

   @Query("getDashboard")
    public DashboardOutput getDashboard() {
        try {
            UUID userId = TEST_USER_ID;
            System.out.println("getDashboard USER => " + userId);
            System.out.println("ANTES DE EXECUTE getDashboard");

            DashboardSummary summary = getDashboardPort.execute(userId);

            System.out.println("DESPUES DE EXECUTE getDashboard => " + summary);

            DashboardOutput output = DtoMapper.toDashboardOutput(summary);

            System.out.println("DESPUES DE MAPPER getDashboard => " + output);

            return output;
        } catch (Exception e) {
            System.out.println("ERROR EN getDashboard => " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @Query("listAttendance")
    //@RolesAllowed("TEAM_MEMBER")
    public AttendancePageOutput listAttendance(@Name("filter") AttendanceFilterInput filter) {

        UUID userId = TEST_USER_ID;
        System.out.println("listAttendance USER => " + userId);

        AttendanceStatus status = filter != null && filter.getStatus() != null
                ? AttendanceStatus.valueOf(filter.getStatus()) : null;

        LocalDate from = filter != null && filter.getFrom() != null
                ? LocalDate.parse(filter.getFrom()) : null;

        LocalDate to = filter != null && filter.getTo() != null
                ? LocalDate.parse(filter.getTo()) : null;

        String search = filter != null ? filter.getSearch() : null;
        int page = filter != null ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() > 0 ? filter.getSize() : 20;

        System.out.println("FILTER => " + filter);

        Page<com.smms.assistance.domain.entity.AttendanceEntity> result =
                listAttendancePort.execute(userId, from, to, status, search, page, size);

        System.out.println("RESULT SIZE => " + result.getItems().size());

        return DtoMapper.toAttendancePage(result);
    }

    @Query("listAbsences")
    //@RolesAllowed("TEAM_MEMBER")
    public java.util.List<AbsenceOutput> listAbsences() {

        UUID userId = TEST_USER_ID;
        System.out.println("listAbsences USER => " + userId);

        var result = listAbsencesPort.execute(userId);

        System.out.println("ABSENCES SIZE => " + result.size());
        System.out.println("ABSENCES RAW => " + result);

        return result.stream().map(a -> {
            System.out.println("MAPPING ABSENCE => " + a);
            return DtoMapper.toAbsenceOutput(a);
        }).toList();
    }

    @Query("listJustifications")
    //@RolesAllowed("TEAM_MEMBER")
    public JustificationPageOutput listJustifications(@Name("filter") JustificationFilterInput filter) {

        UUID userId = TEST_USER_ID;
        System.out.println("listJustifications USER => " + userId);

        JustificationStatus status = filter != null && filter.getStatus() != null
                ? JustificationStatus.valueOf(filter.getStatus()) : null;

        LocalDate from = filter != null && filter.getFrom() != null
                ? LocalDate.parse(filter.getFrom()) : null;

        LocalDate to = filter != null && filter.getTo() != null
                ? LocalDate.parse(filter.getTo()) : null;

        int page = filter != null ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() > 0 ? filter.getSize() : 20;

        Page<com.smms.assistance.domain.entity.JustificationEntity> result =
                listJustificationsPort.execute(userId, status, from, to, page, size);

        System.out.println("JUSTIFICATIONS SIZE => " + result.getItems().size());

        return DtoMapper.toJustificationPage(result);
    }

    @Query("getJustificationStatus")
    //@RolesAllowed("TEAM_MEMBER")
    public JustificationStatusOutput getJustificationStatus(@Name("justificationId") UUID justificationId) {

        UUID userId = TEST_USER_ID;
        System.out.println("getJustificationStatus USER => " + userId);
        System.out.println("JUSTIFICATION ID => " + justificationId);

        return DtoMapper.toJustificationStatusOutput(
                getJustificationStatusPort.execute(userId, justificationId));
    }
}