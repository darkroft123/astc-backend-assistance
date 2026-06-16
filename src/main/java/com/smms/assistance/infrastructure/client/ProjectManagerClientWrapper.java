package com.smms.assistance.infrastructure.client;

import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ProjectManagerClientWrapper {

    private static final Logger LOG = Logger.getLogger(ProjectManagerClientWrapper.class.getName());

    @Inject
    JsonWebToken jwt;

    public List<ProjectOutput> getMyProjects() {
        LOG.info("📡 [Inter-service Call] Initiating GetMyProjects call to Project Manager...");
        long startTime = System.currentTimeMillis();

        try {
            var builder = TypesafeGraphQLClientBuilder.newBuilder()
                    .configKey("project-manager-service");

            if (jwt != null && jwt.getRawToken() != null) {
                builder = builder.header("Authorization", "Bearer " + jwt.getRawToken());
            }

            ProjectManagerClient client = builder.build(ProjectManagerClient.class);
            List<ProjectOutput> response = client.getMyProjects();

            long latency = System.currentTimeMillis() - startTime;
            LOG.info("✅ [Inter-service Call] GetMyProjects completed successfully. Latency: " + latency + "ms. Response status: SUCCESS. Returned " + (response != null ? response.size() : 0) + " projects.");

            return response;
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.log(Level.SEVERE, "❌ [Inter-service Call] GetMyProjects failed. Latency: " + latency + "ms. Response status: FAILURE. Error: " + e.getMessage(), e);
            throw e;
        }
    }
}
