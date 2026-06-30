package com.smms.assistance.application.in;

import com.smms.assistance.domain.entity.AttendanceEntity;
import java.util.UUID;

public interface CheckOutPort {
    AttendanceEntity execute(UUID userId, UUID projectId);
}
