package com.smms.assistance.infrastructure.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

@ApplicationScoped
public class AlertsClient {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AlertsClient.class);
    private final Client client = ClientBuilder.newClient();

    private static final String ALERTS_URL =
        "http://alerts-service:8080/api/analyze-attendance";

    public AlertResponsePayload sendAlert(AlertRequestPayload payload) {
        LOG.info("📤 [Inter-service Call] Payload generated for Alerts Service: attendanceId={}, userId={}, projectId={}", 
            payload.attendanceId, payload.userId, payload.projectId);
        
        long startTime = System.currentTimeMillis();
        try {
            LOG.info("📡 [Inter-service Call] Sending check-in payload to Alerts Service at: {}", ALERTS_URL);
            
            var response = client.target(ALERTS_URL)
                    .request()
                    .post(Entity.json(payload));

            long latency = System.currentTimeMillis() - startTime;

            if (response.getStatus() == 200) {
                AlertResponsePayload result = response.readEntity(AlertResponsePayload.class);
                LOG.info("✅ [Inter-service Call] Alerts Service responded successfully. Latency: {}ms. Result: alert={}, type={}, severity={}, message={}", 
                    latency, result.alert, result.type, result.severity, result.message);
                return result;
            } else {
                String errorBody = response.readEntity(String.class);
                LOG.error("❌ [Inter-service Call] Alerts Service returned error status: {}. Response: {}", response.getStatus(), errorBody);
                return null;
            }
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            LOG.error("❌ [Inter-service Call] Alerts Service call failed. Latency: {}ms. Error: {}", latency, e.getMessage(), e);
            return null;
        }
    }
}