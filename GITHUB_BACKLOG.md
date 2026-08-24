# GitHub Project & Backlog Specification: Motor Telemetry Simulator

Project URL: https://github.com/users/igorhgds/projects/5
Repository: igorhgds/industrial-iot-backend

---

## 📌 Labels Legend
- `epic`: Milestone container
- `infrastructure`: Docker, databases, brokers
- `domain`: Java/Spring core domain logic & Use Cases
- `database`: PostgreSQL & Flyway migrations
- `frontend`: Angular components & real-time WS/SSE
- `simulator`: Python IoT telemetry scripts
- `security`: JWT & Spring Security
- `observability`: Actuator, OpenAPI/Swagger

---

## 🏁 Milestone 1: Infra & Ingestion Pipeline [STATUS: CLOSED / COMPLETED ✅]

### Issue #1: [M1-US01] Docker Infrastructure Setup
- **Type:** Feature / Infra
- **Labels:** `infrastructure`, `m1-infra`
- **Status:** Done ✅
- **Description:**
  Configure local Docker Compose environment with PostgreSQL 16, RabbitMQ 3.13 (Management), and Eclipse Mosquitto v2.
- **Acceptance Criteria:**
  - [x] `docker-compose.yml` created in `industrial-iot-backend` with `postgres`, `rabbitmq`, and `mosquitto` services.
  - [x] Custom `infra/mosquitto/config/mosquitto.conf` listener created for ports 1883 (MQTT) and 9001 (WebSockets).
  - [x] Services running successfully via `docker compose up -d`.
- **Definition of Done (DoD):** Containers running, ports mapped, and persistent volumes configured.

### Issue #2: [M2-US02] Python Motor Telemetry Publisher
- **Type:** Feature / Simulator
- **Labels:** `simulator`, `m1-infra`
- **Status:** Done ✅
- **Description:**
  Build Python simulator script (`simulator/motor_publisher.py`) publishing real-time telemetry metrics (`current`, `voltage`, `temperature`, `vibration`) to Mosquitto MQTT.
- **Acceptance Criteria:**
  - [x] Folder `simulator/` created with `motor_publisher.py` and `requirements.txt`.
  - [x] JSON telemetry payload structure defined with motor ID, timestamp, and sensor readings.
  - [x] Script connects to MQTT broker on `localhost:1883`.
- **Definition of Done (DoD):** Script runs and publishes messages to `factory/line1/motor1/telemetry`.

---

## 🗄️ Milestone 2: Database & Data Modeling [STATUS: IN PROGRESS ⏳]

### Issue #3: [M2-US01] PostgreSQL Schema & Entity Relationship Diagram (ERD)
- **Type:** Feature / Database
- **Labels:** `database`, `m2-db`
- **Description:**
  Design relational database schema for motors, sensors, telemetry readings, and anomaly alerts. Draw ERD diagram.
- **Acceptance Criteria:**
  - [ ] ERD diagram created (Mermaid or DB Diagram format) documenting tables: `motors`, `telemetry_readings`, `anomalies`.
  - [ ] Foreign keys, indexes on timestamp and motor_id for fast time-series queries.
- **Definition of Done (DoD):** ERD documented in `/docs/erd.md` or README.

### Issue #4: [M2-US02] Flyway Database Migration Setup
- **Type:** Feature / Database
- **Labels:** `database`, `m2-db`
- **Description:**
  Create Flyway migration scripts (`V1__init_schema.sql`) inside Spring Boot project (`src/main/resources/db/migration`).
- **Acceptance Criteria:**
  - [ ] `V1__create_tables.sql` creates `motors` and `telemetry_readings` tables.
  - [ ] Flyway runs automatically on Spring Boot application startup without errors.
- **Definition of Done (DoD):** Tables created in PostgreSQL database `industrial_iiot_db`.

---

## ⚙️ Milestone 3: Core Domain & Clean Architecture

### Issue #5: [M3-US01] Clean Architecture Domain Modeling & Entities
- **Type:** Feature / Backend Core
- **Labels:** `domain`, `m3-core`
- **Description:**
  Model core domain entities (`Motor`, `TelemetryValue`, `SensorType`, `AnomalyStatus`) without any Spring framework dependencies (Pure Java).
- **Acceptance Criteria:**
  - [ ] Domain entities encapsulate business validation rules (e.g., max temperature thresholds, vibration spikes).
  - [ ] Zero Spring/JPA annotations inside domain core package.
- **Definition of Done (DoD):** 100% pure Java domain model with unit tests.

### Issue #6: [M3-US02] MQTT Telemetry Consumption & Ingestion Use Case
- **Type:** Feature / Backend Core
- **Labels:** `domain`, `m3-core`
- **Description:**
  Implement MQTT subscriber/consumer in Spring Boot to ingest incoming sensor payloads, pass to `ProcessTelemetryUseCase`, and persist to PostgreSQL.
- **Acceptance Criteria:**
  - [ ] Clean Controller / Messaging Adapter delegating to `ProcessTelemetryUseCase`.
  - [ ] Unit tests for Use Case using Mockito.
  - [ ] Async ingestion handling without blocking main threads.
- **Definition of Done (DoD):** Messages published by Python simulator stored in PostgreSQL `telemetry_readings`.

---

## 💻 Milestone 4: Frontend Integration & Real-Time

### Issue #7: [M4-US01] Angular Project Setup & Base Architecture
- **Type:** Feature / Frontend
- **Labels:** `frontend`, `m4-frontend`
- **Description:**
  Initialize Angular workspace, configure RxJS reactive state management, and setup modern UI design system (dark mode, glassmorphism, responsive grid).
- **Acceptance Criteria:**
  - [ ] Angular project created in frontend directory.
  - [ ] Modular component structure (Telemetry Dashboard, Motor Health Card, Live Charts).

### Issue #8: [M4-US02] WebSockets / SSE Telemetry Dashboard
- **Type:** Feature / Frontend
- **Labels:** `frontend`, `m4-frontend`
- **Description:**
  Connect Angular dashboard to backend via WebSockets or SSE for real-time telemetry streaming and charts.
- **Acceptance Criteria:**
  - [ ] Real-time gauge/chart updating temperature, vibration, current, voltage.
  - [ ] Visual indicators for motor anomaly warnings.

---

## ⚡ Milestone 5: Actuation & Feedback Loop

### Issue #9: [M5-US01] Bi-Directional Actuation Protocol & Python Feedback Receiver
- **Type:** Feature / Bi-Directional
- **Labels:** `simulator`, `m5-actuation`
- **Description:**
  Expand Python simulator to subscribe to actuation topics (e.g., `factory/line1/motor1/command`) to process emergency stops or speed throttling commands sent by Spring Boot.
- **Acceptance Criteria:**
  - [ ] Python simulator listens for commands and updates simulated motor state dynamically.
  - [ ] Spring Boot triggers RabbitMQ/MQTT actuation events when critical thresholds are breached.

---

## 🌡️ Milestone 6: HVAC Control & Thermal Management Module

### Issue #10: [M6-US01] Reactive Thermal Management & HVAC Controller
- **Type:** Feature / HVAC Engine
- **Labels:** `domain`, `m6-hvac`
- **Description:**
  Develop internal HVAC thermal management module that automatically calculates thermal dissipation requirements and activates virtual cooling units when motor temperature exceeds safety thresholds.
- **Acceptance Criteria:**
  - [ ] Reactive Use Case evaluating motor thermal trend.
  - [ ] HVAC state changes logged and emitted to event queue.

---

## 🔒 Milestone 7: Enterprise Security, Observability & Go-to-Market

### Issue #11: [M7-US01] Spring Security JWT & OpenAPI Documentation
- **Type:** Feature / Security
- **Labels:** `security`, `observability`, `m7-enterprise`
- **Description:**
  Implement JWT authentication for REST APIs, Spring Boot Actuator telemetry metrics, and Swagger/OpenAPI documentation.
- **Acceptance Criteria:**
  - [ ] `/api/v1/auth/login` issuing valid JWT tokens.
  - [ ] Swagger UI accessible at `/swagger-ui.html`.
  - [ ] Complete technical `README.md` in English with architecture diagrams.
