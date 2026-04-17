package com.smms.assistance.application.usecase;

import com.smms.assistance.application.in.ListJustificationsPort;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.model.JustificationStatus;
import com.smms.assistance.domain.model.Page;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListJustificationsUseCase implements ListJustificationsPort {

    @Inject JustificationRepository justificationRepository;

    @Override
    public Page<JustificationEntity> execute(UUID userId, JustificationStatus status,
                                              LocalDate from, LocalDate to,
                                              int page, int size) {
        List<JustificationEntity> items = justificationRepository.findByUser(userId, status, from, to, page, size);
        long total = justificationRepository.countByUser(userId, status, from, to);
        int totalPages = (int) Math.ceil((double) total / size);

        return Page.<JustificationEntity>builder()
                .items(items)
                .totalItems(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }
}
