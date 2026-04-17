package com.smms.assistance.application.in;

import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.model.JustificationStatus;
import com.smms.assistance.domain.model.Page;
import java.time.LocalDate;
import java.util.UUID;

public interface ListJustificationsPort {
    Page<JustificationEntity> execute(UUID userId, JustificationStatus status,
                                       LocalDate from, LocalDate to,
                                       int page, int size);
}
