package com.smms.assistance.infrastructure.client;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Name;

import java.util.List;
import java.util.UUID;

@GraphQLClientApi(configKey = "project-manager-service")
public interface ProjectManagerClient {

    @Query("GetMyProjects")
    List<ProjectOutput> getMyProjects();

    @Mutation
    void createAlert(
            @Name("userId") UUID userId,
            @Name("projectId") UUID projectId,
            @Name("type") String type,
            @Name("detail") String detail,
            @Name("latitude") Double latitude,
            @Name("longitude") Double longitude
    );
}