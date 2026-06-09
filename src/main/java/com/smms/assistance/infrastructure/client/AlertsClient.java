package com.smms.assistance.infrastructure.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

@ApplicationScoped
public class AlertsClient {

    private final Client client = ClientBuilder.newClient();

    private static final String ALERTS_URL =
        "http://astc-cn-main-alerts-service:8080/create-alert";

    public void sendAlert(Object payload) {
        try {
            client.target(ALERTS_URL)
                    .request()
                    .post(Entity.json(payload));

            System.out.println("🚨 ALERT SENT TO ALERTS SERVICE");

        } catch (Exception e) {
            System.out.println("❌ ERROR SENDING ALERT: " + e.getMessage());
        }
    }
}