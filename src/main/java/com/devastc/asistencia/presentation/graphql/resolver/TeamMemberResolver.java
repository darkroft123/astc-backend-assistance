package com.devastc.asistencia.presentation.graphql.resolver;

import com.devastc.asistencia.application.command.JustifyAbsenceCommand;
import com.devastc.asistencia.application.command.RegisterAttendanceCommand;
import com.devastc.asistencia.application.command.SaveJustificationCommand;
import com.devastc.asistencia.application.query.GetDashboardQuery;
import com.devastc.asistencia.application.query.ListAbsencesQuery;
import com.devastc.asistencia.application.query.ListAttendanceQuery;
import com.devastc.asistencia.application.query.ListJustificationsQuery;
import com.devastc.asistencia.application.usecase.GetDashboardUseCase;
import com.devastc.asistencia.application.usecase.JustifyAbsenceUseCase;
import com.devastc.asistencia.application.usecase.ListAbsencesUseCase;
import com.devastc.asistencia.application.usecase.ListAttendanceUseCase;
import com.devastc.asistencia.application.usecase.ListJustificationsUseCase;
import com.devastc.asistencia.application.usecase.RegisterAttendanceUseCase;
import com.devastc.asistencia.application.usecase.SaveJustificationUseCase;
import com.devastc.asistencia.presentation.graphql.input.FilterInput;
import com.devastc.asistencia.presentation.graphql.input.RegisterAttendanceInput;
import com.devastc.asistencia.presentation.graphql.input.SaveJustificationInput;
import com.devastc.asistencia.presentation.graphql.mapper.GraphQLMapper;
import com.devastc.asistencia.presentation.graphql.output.AbsenceOutput;
import com.devastc.asistencia.presentation.graphql.output.AttendanceOutput;
import com.devastc.asistencia.presentation.graphql.output.DashboardOutput;
import com.devastc.asistencia.presentation.graphql.output.JustificationOutput;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.stream.Collectors;

@GraphQLApi
public class TeamMemberResolver {

    private static final String TEST_USER_ID = "11111111-1111-1111-1111-111111111111";

    @Inject
    GetDashboardUseCase getDashboardUseCase;

    @Inject
    ListAttendanceUseCase listAttendanceUseCase;

    @Inject
    ListAbsencesUseCase listAbsencesUseCase;

    @Inject
    JustifyAbsenceUseCase justifyAbsenceUseCase;

    @Inject
    ListJustificationsUseCase listJustificationsUseCase;

    @Inject
    RegisterAttendanceUseCase registerAttendanceUseCase;

    @Inject
    SaveJustificationUseCase saveJustificationUseCase;

    @Query("getDashboard")
    public DashboardOutput getDashboard() {
        GetDashboardQuery q = new GetDashboardQuery();
        q.setUserId(TEST_USER_ID);
        return GraphQLMapper.toOutput(getDashboardUseCase.execute(q));
    }

    @Query("listAttendance")
    public List<AttendanceOutput> listAttendance(@Name("filter") FilterInput filter) {
        ListAttendanceQuery q = new ListAttendanceQuery();
        q.setUserId(TEST_USER_ID);

        if (filter != null) {
            q.setFromDate(filter.getFromDate());
            q.setToDate(filter.getToDate());
            q.setProjectId(filter.getProjectId());
            q.setStatus(filter.getStatus());
            q.setPage(filter.getPage() != null ? filter.getPage() : 0);
            q.setSize(filter.getSize() != null ? filter.getSize() : 10);
        }

        return listAttendanceUseCase.execute(q)
                .stream()
                .map(GraphQLMapper::toOutput)
                .collect(Collectors.toList());
    }

    @Query("listAbsences")
    public List<AbsenceOutput> listAbsences(@Name("filter") FilterInput filter) {
        ListAbsencesQuery q = new ListAbsencesQuery();
        q.setUserId(TEST_USER_ID);

        if (filter != null) {
            q.setFromDate(filter.getFromDate());
            q.setToDate(filter.getToDate());
        }

        return listAbsencesUseCase.execute(q)
                .stream()
                .map(GraphQLMapper::toOutput)
                .collect(Collectors.toList());
    }

    @Mutation("justifyAbsence")
    public boolean justifyAbsence(@Name("absenceId") String absenceId) {
        JustifyAbsenceCommand cmd = new JustifyAbsenceCommand();
        cmd.setAbsenceId(absenceId);
        cmd.setUserId(TEST_USER_ID);
        return justifyAbsenceUseCase.execute(cmd);
    }

    @Query("listJustifications")
    public List<JustificationOutput> listJustifications(@Name("filter") FilterInput filter) {
        ListJustificationsQuery q = new ListJustificationsQuery();
        q.setUserId(TEST_USER_ID);

        if (filter != null) {
            q.setStatusFilter(filter.getStatus());
        }

        return listJustificationsUseCase.execute(q)
                .stream()
                .map(GraphQLMapper::toOutput)
                .collect(Collectors.toList());
    }

    @Mutation("registerAttendance")
    public AttendanceOutput registerAttendance(@Name("input") RegisterAttendanceInput input) {
        RegisterAttendanceCommand cmd = new RegisterAttendanceCommand();
        cmd.setUserId(TEST_USER_ID);
        cmd.setProjectId(input.getProjectId());
        cmd.setLatitude(input.getLatitude());
        cmd.setLongitude(input.getLongitude());
        cmd.setPhotoUrl(input.getPhotoUrl());

        return GraphQLMapper.toOutput(registerAttendanceUseCase.execute(cmd));
    }

    @Mutation("saveJustification")
    public JustificationOutput saveJustification(@Name("input") SaveJustificationInput input) {
        SaveJustificationCommand cmd = new SaveJustificationCommand();
        cmd.setAbsenceId(input.getAbsenceId());
        cmd.setUserId(TEST_USER_ID);
        cmd.setDescription(input.getDescription());
        cmd.setDocumentUrl(input.getDocumentUrl());

        return GraphQLMapper.toOutput(saveJustificationUseCase.execute(cmd));
    }
}