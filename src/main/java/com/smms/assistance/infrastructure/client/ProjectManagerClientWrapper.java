package com.smms.assistance.infrastructure.client;

import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ProjectManagerClientWrapper {

    private static final Logger LOG = Logger.getLogger(ProjectManagerClientWrapper.class.getName());

    @Inject
    JsonWebToken jwt;

    public List<ProjectOutput> getMyProjects() {
        LOG.info("📡 Calling Project Manager: GetMyProjects...");
        long startTime = System.currentTimeMillis();

        try {
            var builder = TypesafeGraphQLClientBuilder.newBuilder()
                    .configKey("project-manager-service");

            if (jwt != null && jwt.getRawToken() != null) {
                builder = builder.header("Authorization", "Bearer " + jwt.getRawToken());
            }

            ProjectManagerClient client = builder.build(ProjectManagerClient.class);

            List<ProjectOutput> response = client.getMyProjects();

            LOG.info("✅ GetMyProjects OK (" +
                    (System.currentTimeMillis() - startTime) + "ms)");

            return response;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "❌ GetMyProjects failed", e);
            throw e;
        }
    }

    public void createAlert(
            UUID userId,
            UUID projectId,
            String type,
            String detail,
            Double latitude,
            Double longitude
    ) {
        LOG.info("📡 Calling Project Manager: createAlert...");
        long startTime = System.currentTimeMillis();

        try {
            var builder = TypesafeGraphQLClientBuilder.newBuilder()
                    .configKey("project-manager-service");

            if (jwt != null && jwt.getRawToken() != null) {
                builder = builder.header("Authorization", "Bearer " + jwt.getRawToken());
            }

            ProjectManagerClient client = builder.build(ProjectManagerClient.class);

            client.createAlert(userId, projectId, type, detail, latitude, longitude);

            LOG.info("✅ createAlert OK (" +
                    (System.currentTimeMillis() - startTime) + "ms)");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "❌ createAlert failed", e);
            // no throw (check-in must NOT break)
        }
    }
}