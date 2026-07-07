package com.smms.assistance.infrastructure.repository;

import com.smms.assistance.domain.entity.OutboxEventEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<OutboxEventEntity, UUID> {

    private static final Logger LOG = Logger.getLogger(OutboxRepository.class);

    public List<OutboxEventEntity> findUnpublished() {
        List<OutboxEventEntity> results = list("published", false);
        if (!results.isEmpty()) {
            LOG.infof("[OUTBOX] Found %d unpublished events", results.size());
        }
        return results;
    }
}
