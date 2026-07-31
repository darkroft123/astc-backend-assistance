package com.smms.assistance.infrastructure.client;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Name;
import io.quarkus.cache.CacheResult;

@GraphQLClientApi(configKey = "auth-service")
public interface AuthClient {

    @Query("getUser")
    @CacheResult(cacheName = "users")
    UserOutput getUser(@Name("id") String id);
}
