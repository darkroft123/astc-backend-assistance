package com.smms.assistance.infrastructure.messaging;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.smms.assistance.domain.entity.AbsenceEntity;
import com.smms.assistance.domain.entity.AttendanceEntity;
import com.smms.assistance.domain.entity.JustificationEntity;
import com.smms.assistance.domain.entity.OutboxEventEntity;
import com.smms.assistance.infrastructure.repository.AbsenceRepository;
import com.smms.assistance.infrastructure.repository.AttendanceRepository;
import com.smms.assistance.infrastructure.repository.JustificationRepository;
import com.smms.assistance.infrastructure.repository.OutboxRepository;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OutboxEventProcessor {

    private static final Logger LOG = Logger.getLogger(OutboxEventProcessor.class);

    private static final java.util.Map<String, String> STATUS_TRANSLATIONS = new java.util.HashMap<>();
    static {
        STATUS_TRANSLATIONS.put("ON_TIME", "A tiempo");
        STATUS_TRANSLATIONS.put("ON_TIME_CHECKED_OUT", "A tiempo");
        STATUS_TRANSLATIONS.put("LATE", "Tarde");
        STATUS_TRANSLATIONS.put("LATE_CHECKED_OUT", "Tarde");
        STATUS_TRANSLATIONS.put("EARLY", "Fuera de horario");
        STATUS_TRANSLATIONS.put("OUTSIDE_SCHEDULE", "Fuera de horario");
        STATUS_TRANSLATIONS.put("EARLY_DEPARTURE", "Salida temprana");
        STATUS_TRANSLATIONS.put("CHECKED_IN", "Registrado");
        STATUS_TRANSLATIONS.put("PRESENTE", "Presente");
        STATUS_TRANSLATIONS.put("FALTA", "Falta");
        STATUS_TRANSLATIONS.put("UNJUSTIFIED", "No justificada");
        STATUS_TRANSLATIONS.put("PENDING", "Borrador");
        STATUS_TRANSLATIONS.put("SUBMITTED", "Enviado");
        STATUS_TRANSLATIONS.put("OBSERVATION", "Observado");
        STATUS_TRANSLATIONS.put("APPROVED", "Aprobado");
        STATUS_TRANSLATIONS.put("REJECTED", "Rechazado");
    }

    private static String translateStatus(String rawStatus) {
        if (rawStatus == null) return "";
        String translated = STATUS_TRANSLATIONS.get(rawStatus.toUpperCase());
        return translated != null ? translated : rawStatus;
    }

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    AttendanceRepository attendanceRepository;

    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    JustificationRepository justificationRepository;

    @Inject
    @Channel("notification-events")
    Emitter<String> notificationEmitter;

    @Inject
    com.smms.assistance.infrastructure.client.AuthClient authClient;

    @ConfigProperty(name = "event.notification.template.attendance_registered", defaultValue = "AST00001")
    String attendanceTemplateCode;

    @ConfigProperty(name = "event.notification.template.attendance_early", defaultValue = "AST00002")
    String attendanceEarlyTemplateCode;

    @ConfigProperty(name = "event.notification.template.attendance_late", defaultValue = "AST00003")
    String attendanceLateTemplateCode;

    @ConfigProperty(name = "event.notification.template.attendance_outside_hours", defaultValue = "AST00004")
    String attendanceOutsideHoursTemplateCode;

    @ConfigProperty(name = "event.notification.template.absence_late", defaultValue = "AST00005")
    String absenceLateTemplateCode;

    @ConfigProperty(name = "event.notification.template.absence_late_pm", defaultValue = "AST00006")
    String absenceLatePmTemplateCode;

    @ConfigProperty(name = "event.notification.template.absence_no_record", defaultValue = "AST00007")
    String absenceNoRecordTemplateCode;

    @ConfigProperty(name = "event.notification.template.absence_no_record_pm", defaultValue = "AST00008")
    String absenceNoRecordPmTemplateCode;

    @ConfigProperty(name = "event.notification.template.checkout_registered", defaultValue = "AST00009")
    String checkoutTemplateCode;

    @ConfigProperty(name = "event.notification.template.checkout_early", defaultValue = "AST00010")
    String checkoutEarlyTemplateCode;

    @ConfigProperty(name = "event.notification.template.absence_early_checkout", defaultValue = "AST00011")
    String absenceEarlyCheckoutTemplateCode;

    @ConfigProperty(name = "event.notification.template.justification_submitted", defaultValue = "AST00012")
    String justificationSubmittedTemplateCode;

    @ConfigProperty(name = "event.notification.template.justification_received", defaultValue = "AST00013")
    String justificationReceivedTemplateCode;

    @ConfigProperty(name = "event.notification.template.justification_resubmitted", defaultValue = "AST00014")
    String justificationResubmittedTemplateCode;

    @ConfigProperty(name = "event.notification.default.email", defaultValue = "dev-notification@astc.local")
    String defaultEmail;

    @Inject
    Vertx vertx;

    void init(@Observes StartupEvent ev) {
        vertx.setPeriodic(5000, id -> vertx.executeBlocking(
            promise -> {
                try {
                    processEvents();
                    promise.complete();
                } catch (Exception e) {
                    LOG.errorf(e, "Error processing outbox events");
                    promise.fail(e);
                }
            },
            false
        ));
    }

    @Transactional
    void processEvents() {
        List<OutboxEventEntity> events = outboxRepository.findUnpublished();
        if (events.isEmpty()) {
            return;
        }

        LOG.infof("📬 [Outbox] Found %d unpublished outbox events to process", events.size());

        for (OutboxEventEntity event : events) {
            try {
                LOG.infof("📬 [Outbox] Processing event ID=%s, type=%s, payload=%s", 
                        event.getId(), event.getEventType(), event.getPayload());
                JsonObject notificationMsg = mapToNotification(event);
                if (notificationMsg != null) {
                    LOG.infof("📬 [Outbox] Dispatching payload to RabbitMQ channel 'notification-events': %s", notificationMsg.encode());
                    notificationEmitter.send(notificationMsg.encode());
                    LOG.infof("✅ [Outbox] Successfully published outbox event %s (type=%s) as %s to=%s",
                            event.getId(), event.getEventType(),
                            notificationMsg.getString("notificationCode"),
                            notificationMsg.getString("toEmail"));
                } else {
                    LOG.warnf("⚠ [Outbox] Event %s mapped to a null notification payload", event.getId());
                }
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
            } catch (Exception e) {
                LOG.errorf(e, "❌ [Outbox] Failed to process outbox event %s (type=%s)",
                        event.getId(), event.getEventType());
            }
        }
    }

    private void enrichUserDetails(JsonObject payload, String userId) {
        String toEmail = payload.getString("userEmail", "");
        if (toEmail == null || toEmail.isBlank()) {
            if (userId != null && !userId.isBlank()) {
                try {
                    com.smms.assistance.infrastructure.client.UserOutput user = authClient.getUser(userId);
                    if (user != null) {
                        payload.put("userEmail", user.email);
                        payload.put("userName", user.username);
                    }
                } catch (Exception e) {
                    LOG.warnf("Could not fetch user details from auth-service for userId=%s: %s", userId, e.getMessage());
                }
            }
        }
    }

    private JsonObject mapToNotification(OutboxEventEntity event) {
        JsonObject payload = new JsonObject(event.getPayload());
        String userId = payload.getString("userId");

        if (userId == null || userId.isBlank()) {
            LOG.warnf("Skipping outbox event %s: missing userId in payload", event.getId());
            return null;
        }

        enrichUserDetails(payload, userId);

        switch (event.getEventType()) {
            case "attendance_registered":
                return buildMessage(event, payload, userId, attendanceTemplateCode);
            case "attendance_early":
                return buildMessage(event, payload, userId, attendanceEarlyTemplateCode);
            case "attendance_late":
                return buildMessage(event, payload, userId, attendanceLateTemplateCode);
            case "attendance_outside_hours":
                return buildMessage(event, payload, userId, attendanceOutsideHoursTemplateCode);
            case "absence_late":
                return buildMessage(event, payload, userId, absenceLateTemplateCode);
            case "absence_late_pm":
                return buildMessage(event, payload, userId, absenceLatePmTemplateCode);
            case "absence_no_record":
                return buildMessage(event, payload, userId, absenceNoRecordTemplateCode);
            case "absence_no_record_pm":
                return buildMessage(event, payload, userId, absenceNoRecordPmTemplateCode);
            case "checkout_registered":
                return buildMessage(event, payload, userId, checkoutTemplateCode);
            case "checkout_early":
                return buildMessage(event, payload, userId, checkoutEarlyTemplateCode);
            case "absence_early_checkout":
                return buildMessage(event, payload, userId, absenceEarlyCheckoutTemplateCode);
            case "SendNotificationByQueue":
                return buildJustificationMessage(event, payload, userId, justificationSubmittedTemplateCode);
            case "justification_submitted":
                return buildJustificationMessage(event, payload, userId, justificationSubmittedTemplateCode);
            case "justification_received":
                return buildJustificationMessage(event, payload, userId, justificationReceivedTemplateCode);
            case "justification_resubmitted":
                return buildJustificationMessage(event, payload, userId, justificationResubmittedTemplateCode);
            default:
                LOG.warnf("Unknown event type '%s' for outbox event %s, skipping",
                        event.getEventType(), event.getId());
                return null;
        }
    }

    private JsonObject buildMessage(OutboxEventEntity event, JsonObject payload, String userId, String templateCode) {
        JsonObject variables = new JsonObject();
        variables.put("USER_ID", userId);

        String attendanceId = payload.getString("attendanceId");
        if (attendanceId != null) {
            variables.put("ATTENDANCE_ID", attendanceId);
            AttendanceEntity attendance = attendanceRepository.findByIdOptional(UUID.fromString(attendanceId)).orElse(null);
            if (attendance != null) {
                if (attendance.getProjectId() != null) {
                    variables.put("PROJECT_ID", attendance.getProjectId().toString());
                }
                if (attendance.getCheckIn() != null) {
                    variables.put("CHECKIN_TIME", attendance.getCheckIn().toString());
                }
                if (attendance.getDate() != null) {
                    variables.put("ATTENDANCE_DATE", attendance.getDate().toString());
                }
                variables.put("STATUS", translateStatus(attendance.getStatus()));
            }
        }

        String toName = payload.getString("userName", "");
        String toEmail = payload.getString("userEmail", "");
        if (toEmail.isBlank()) {
            toEmail = variables.getString("USER_EMAIL", defaultEmail);
        }

        return formatEmailMessage(templateCode, userId, toEmail, toName, variables);
    }

    private JsonObject buildJustificationMessage(OutboxEventEntity event, JsonObject payload, String userId, String templateCode) {
        JsonObject variables = new JsonObject();
        variables.put("USER_ID", userId);

        String justificationId = payload.getString("justificationId");
        String absenceId = payload.getString("absenceId");
        if (justificationId != null) {
            variables.put("JUSTIFICATION_ID", justificationId);
        }
        if (absenceId != null) {
            variables.put("ABSENCE_ID", absenceId);
            AbsenceEntity absence = absenceRepository.findByIdOptional(UUID.fromString(absenceId)).orElse(null);
            if (absence != null) {
                if (absence.getProjectId() != null) {
                    variables.put("PROJECT_ID", absence.getProjectId().toString());
                }
                if (absence.getDate() != null) {
                    variables.put("ABSENCE_DATE", absence.getDate().toString());
                }
            }
        }

        if (justificationId != null) {
            JustificationEntity justification = justificationRepository.findByIdOptional(UUID.fromString(justificationId)).orElse(null);
            if (justification != null) {
                variables.put("STATUS", translateStatus(justification.getStatus()));
            }
        }

        String toName = payload.getString("userName", "");
        String toEmail = payload.getString("userEmail", "");
        if (toEmail.isBlank()) {
            toEmail = defaultEmail;
        }

        return formatEmailMessage(templateCode, userId, toEmail, toName, variables);
    }

    private JsonObject formatEmailMessage(String notificationCode, String userId, String toEmail, String toName, JsonObject variables) {
        JsonObject msg = new JsonObject();
        msg.put("notificationCode", notificationCode);
        msg.put("type", "EMAIL");
        msg.put("userId", userId);
        msg.put("toEmail", toEmail);
        msg.put("toName", toName);
        msg.put("variables", variables);
        return msg;
    }
}
