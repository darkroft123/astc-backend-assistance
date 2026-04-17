package com.smms.assistance.application.in;

import com.smms.assistance.domain.entity.AbsenceEntity;
import java.util.List;
import java.util.UUID;

public interface ListAbsencesPort {
    List<AbsenceEntity> execute(UUID userId);
}
