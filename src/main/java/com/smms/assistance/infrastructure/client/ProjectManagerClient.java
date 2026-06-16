package com.smms.assistance.infrastructure.client;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;
import java.util.List;

@GraphQLClientApi(configKey = "project-manager-service")
public interface ProjectManagerClient {

    @Query("GetMyProjects")
    List<ProjectOutput> getMyProjects();
}
