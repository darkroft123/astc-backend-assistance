package com.smms.assistance.infrastructure.storage;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@ApplicationScoped
public class S3StorageAdapter {

    @ConfigProperty(name = "app.storage.endpoint")
    String endpoint;

    @ConfigProperty(name = "app.storage.access-key")
    String accessKey;

    @ConfigProperty(name = "app.storage.secret-key")
    String secretKey;

    @ConfigProperty(name = "app.storage.region")
    String region;

    @ConfigProperty(name = "app.storage.bucket")
    String bucket;

    private S3Client s3Client;

    @PostConstruct
    void init() {
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();

        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
    }

    public String upload(String folder, String originalFilename, InputStream data, long size, String contentType) {
        String key = folder + "/" + UUID.randomUUID() + "_" + originalFilename;
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(data, size));
        return endpoint + "/" + bucket + "/" + key;
    }
}
