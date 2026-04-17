package com.smms.assistance.infrastructure.messaging;

import com.smms.assistance.domain.entity.OutboxEventEntity;
import com.smms.assistance.infrastructure.repository.OutboxRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

/**
 * Writes domain events to the outbox table within the same transaction.
 * A separate scheduler/poller will read and publish them to RabbitMQ.
 */
@ApplicationScoped
public class OutboxEventPublisher {

    @Inject
    OutboxRepository outboxRepository;

    @Transactional
    public void publish(String aggregateType, UUID aggregateId, String eventType, String payload) {
        OutboxEventEntity event = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .published(false)
                .build();
        outboxRepository.persist(event);
    }
}
