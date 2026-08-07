package com.smms.assistance.infrastructure.messaging;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class UserEventConsumer {

    private static final Logger LOG = Logger.getLogger(UserEventConsumer.class);

    @Inject
    @CacheName("users")
    Cache usersCache;

    @Incoming("user-events")
    public void process(String messageJson) {
        try {
            LOG.infof("📥 [RabbitMQ] Received user event: %s", messageJson);
            JsonObject event = new JsonObject(messageJson);
            String eventType = event.getString("eventType");
            String userIdStr = event.getString("userId");

            if (userIdStr != null) {
                // Invalidate specific user cache entry (keyed by String in AuthClient.getUser)
                usersCache.invalidate(userIdStr).await().indefinitely();
                LOG.infof("⚡ [CACHE EVICT] Invalidated users cache for: %s", userIdStr);
            }

        } catch (Exception e) {
            LOG.error("❌ Error processing user cache invalidation event", e);
        }
    }
}
