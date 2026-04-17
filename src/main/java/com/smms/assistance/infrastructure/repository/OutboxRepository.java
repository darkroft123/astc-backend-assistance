package com.smms.assistance.infrastructure.repository;

import com.smms.assistance.domain.entity.OutboxEventEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<OutboxEventEntity, UUID> {

    public List<OutboxEventEntity> findUnpublished() {
        return list("published", false);
    }
}
