================================================================================
🧠 ASSISTANCE SERVICE - ESPECIFICACIÓN DE DOMINIO (ENTERPRISE)
================================================================================

📌 RESUMEN DEL SERVICIO
--------------------------------------------------------------------------------
El servicio de asistencia (assistance-service) es un microservicio de dominio
encargado de gestionar todo el ciclo de vida de la asistencia de usuarios,
ausencias, justificaciones y control de anomalías dentro del sistema ASTC.

Está diseñado con arquitectura empresarial basada en eventos y hexagonal.

Tecnologías:
- Java 21 + Quarkus
- API GraphQL (MicroProfile)
- PostgreSQL (base de datos propia)
- RabbitMQ (eventos)
- Redis (opcional para cache)
- MinIO (almacenamiento de archivos)
- JWT (validación externa desde auth-service)

--------------------------------------------------------------------------------

🏗️ ARQUITECTURA INTERNA (HEXAGONAL)
--------------------------------------------------------------------------------

application/
├── in/                 (puertos de entrada)
├── service/            (casos de uso)

domain/
├── entities/           (entidades del dominio)
├── value-objects/      (objetos inmutables)

infrastructure/
├── controller/graphql  (resolvers GraphQL)
├── persistence         (adaptadores de base de datos)
├── messaging           (RabbitMQ producer/consumer)
├── security            (JWT + AuthContext)

--------------------------------------------------------------------------------

🎯 RESPONSABILIDADES DEL SERVICIO
--------------------------------------------------------------------------------

Este microservicio es responsable de:

✔ Registro de asistencia (check-in / check-out)
✔ Gestión de ausencias
✔ Creación y revisión de justificaciones
✔ Consulta de dashboards de asistencia
✔ Integración con sistema de IA para detección de fraude
✔ Publicación de eventos de dominio en tiempo real

--------------------------------------------------------------------------------

🌐 API GRAPHQL
--------------------------------------------------------------------------------

📍 Endpoint principal:
/graphql

--------------------------------------------------------------------------------
MUTACIONES
--------------------------------------------------------------------------------
- RegisterAttendance (Registrar asistencia)
- JustifyAbsence (Justificar ausencia)
- SaveJustification (Guardar justificación)

--------------------------------------------------------------------------------
CONSULTAS
--------------------------------------------------------------------------------
- GetDashboard (Obtener dashboard)
- ListAttendance (Listar asistencias)
- ListAbsences (Listar ausencias)
- ListJustifications (Listar justificaciones)
- GetJustificationStatus (Estado de justificación)

--------------------------------------------------------------------------------

🔐 SEGURIDAD
--------------------------------------------------------------------------------

Autenticación:
- JWT emitido por el servicio auth-service (Better Auth)

Claims del token:
- sub → ID de usuario
- email → correo del usuario
- role → rol del usuario

Reglas de seguridad:
- Todo endpoint requiere JWT válido
- El userId se obtiene desde AuthContext
- Nunca se confía en datos enviados por el cliente

Roles:
- TEAM_MEMBER (usuario principal del sistema)

--------------------------------------------------------------------------------

🗄️ BASE DE DATOS (POSTGRESQL + FLYWAY)
--------------------------------------------------------------------------------

Propiedad:
- Base de datos exclusiva del servicio
- No existen foreign keys hacia otros microservicios
- Las referencias externas (user_id, project_id) son UUID sin relación directa

--------------------------------------------------------------------------------

TABLAS PRINCIPALES
--------------------------------------------------------------------------------

1. attendances (asistencias)
--------------------------------------------------------------------------------
- id (UUID)
- user_id (UUID externo)
- project_id (UUID externo)
- date (fecha)
- check_in (hora entrada)
- check_out (hora salida)
- status (estado)
- latitude / longitude (ubicación)
- photo_url (evidencia fotográfica)
- created_at / updated_at

--------------------------------------------------------------------------------

2. absences (ausencias)
--------------------------------------------------------------------------------
- id (UUID)
- user_id (UUID externo)
- project_id (UUID externo)
- date (fecha)
- type (tipo de ausencia)
- justified (booleano)

--------------------------------------------------------------------------------

3. justifications (justificaciones)
--------------------------------------------------------------------------------
- id (UUID)
- absence_id (UUID)
- user_id (UUID externo)
- description (texto)
- document_url (archivo adjunto)
- status (PENDING | APPROVED | REJECTED)
- comment (comentario del revisor)
- submitted_at (fecha envío)
- reviewed_at (fecha revisión)
- reviewed_by (UUID del revisor)

--------------------------------------------------------------------------------

4. alerts (alertas del sistema)
--------------------------------------------------------------------------------
- id (UUID)
- user_id (UUID externo)
- project_id (UUID externo)
- type (tipo de alerta)
    - FRAUDE_FACIAL
    - ASISTENCIA_TARDÍA
    - UBICACIÓN_ANÓMALA
- status (PENDING | RESOLVED)
- detail (detalle del evento)
- latitude / longitude
- created_at / updated_at

--------------------------------------------------------------------------------

📡 EVENTOS (RABBITMQ)
--------------------------------------------------------------------------------

Exchange:
- assistance.events

Eventos publicados:

- attendance.registered
- absence.created
- justification.submitted
- justification.approved
- justification.rejected
- alert.created

Objetivo:
- Integración con sistemas externos
- Notificaciones en tiempo real
- Analítica y monitoreo
- Integración con IA

--------------------------------------------------------------------------------

🧠 REGLAS DE NEGOCIO
--------------------------------------------------------------------------------

1. Un usuario solo puede registrar su propia asistencia
2. Solo puede existir una justificación activa por ausencia
3. El rol TEAM_MEMBER es obligatorio para operar en el sistema
4. Toda asistencia fuera de horario o ubicación genera alerta
5. El sistema de IA valida rostro antes de aceptar asistencia
6. Toda anomalía debe generar un evento de alerta automáticamente

--------------------------------------------------------------------------------

📦 ALMACENAMIENTO (MINIO)
--------------------------------------------------------------------------------

Bucket:
- assistance-storage

Uso:
- Fotos de asistencia
- Evidencias de justificación
- Resultados del análisis de IA

--------------------------------------------------------------------------------

⚡ CACHE (REDIS - OPCIONAL)
--------------------------------------------------------------------------------

Uso:
- Dashboard de asistencia
- Consultas frecuentes
- Optimización de consultas GraphQL

--------------------------------------------------------------------------------

📊 OBSERVABILIDAD
--------------------------------------------------------------------------------

- Logs estructurados
- Trazabilidad distribuida (OpenTelemetry opcional)
- Métricas:
  - tasa de asistencia
  - cantidad de alertas generadas
  - tiempo de revisión de justificaciones
  - detección de fraude

--------------------------------------------------------------------------------

🔁 INTEGRACIONES
--------------------------------------------------------------------------------

Este servicio se integra con:

- auth-service → validación JWT
- AI worker (Python) → detección de fraude facial
- RabbitMQ → sistema de eventos
- PostgreSQL → persistencia de datos
- MinIO → almacenamiento de archivos
- backoffice-service → monitoreo administrativo

--------------------------------------------------------------------------------

🚀 RESUMEN
--------------------------------------------------------------------------------

El assistance-service es:

✔ Un microservicio de dominio
✔ API GraphQL
✔ Sistema basado en eventos
✔ Dueño exclusivo de su base de datos
✔ Integrado con IA para control de asistencia
✔ Sistema empresarial de auditoría y control

================================================================================
FIN DEL ESPEC
================================================================================