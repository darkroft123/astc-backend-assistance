package com.smms.assistance.application.in;

import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.model.Page;
import com.smms.assistance.domain.model.AttendanceStatus;
import java.time.LocalDate;
import java.util.UUID;

public interface ListAttendancePort {
    Page<AttendanceEntity> execute(UUID userId, LocalDate from, LocalDate to,
                                   AttendanceStatus status, String search,
                                   int page, int size);
}
