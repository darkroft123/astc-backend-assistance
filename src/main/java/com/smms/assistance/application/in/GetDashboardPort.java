package com.smms.assistance.application.in;

import com.smms.assistance.domain.model.DashboardSummary;
import java.time.LocalDate;
import java.util.UUID;

public interface GetDashboardPort {
    DashboardSummary execute(UUID userId, LocalDate fromDate, LocalDate toDate);
}
