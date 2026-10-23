# 🏭 Industrial IIoT - Motor Telemetry & Machinery Health Monitoring

![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.x-brightgreen?style=for-the-badge&logo=springboot)
![Docker](https://img.shields.io/badge/Docker-24.0+-blue?style=for-the-badge&logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![TimescaleDB](https://img.shields.io/badge/TimescaleDB-Hypertable-yellow?style=for-the-badge&logo=timescale)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange?style=for-the-badge&logo=rabbitmq)
![MQTT](https://img.shields.io/badge/Eclipse_Mosquitto-2.0-purple?style=for-the-badge&logo=eclipse-ide)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

An **Industry 4.0 Industrial IoT (IIoT)** backend platform built for real-time electric motor telemetry processing, machinery health management, anomaly diagnosis, and automated thermal feedback actuation loops.

---

## 📌 Architectural Principles

- **Event-Driven Architecture (EDA):** Dual-broker strategy utilizing **Mosquitto (MQTT)** for lightweight real-time telemetry ingestion and actuation, and **RabbitMQ (AMQP)** for transactional, guaranteed delivery of critical anomaly alerts and maintenance work orders (Ordens de Serviço).
- **Three-Tier Data Architecture Separation:** 
  1. *Configuration & Master Data:* Relational tables (`equipments`, `sensors`, `actuators`, `gateways`, `rule_groups`, `rule_conditions`).
  2. *Current State & Operational:* Fast $O(1)$ equipment status lookup (`equipment_state`), multi-sensor alert snapshots (`alerts`, `alert_sensor_snapshots`), and maintenance work orders (`work_orders`).
  3. *Historical Stream:* TimescaleDB Hypertable (`telemetry_data`) for high-throughput time-series telemetry scale.
- **Clean & Hexagonal Architecture:** Strict Separation of Concerns. Core domain business logic is decoupled from frameworks, ensuring testability and longevity.
- **Quality Assurance & Verification:** Comprehensive unit and integration test coverage using JUnit 5, Mockito, and Testcontainers.
- **Container-First Environment:** 100% conteinerized local environment managed via Docker Compose.

---

## 🏗 System Architecture Diagram

```mermaid
flowchart TD
    subgraph edge_sim ["Edge & Simulators"]
        MS["Python Motor Simulator<br/>(Gateway + Multi-Sensor Payload)"]
    end

    subgraph msg_broker ["Messaging Broker Layer"]
        MQTT["📡 Mosquitto MQTT Broker (MQTT :1883 / WS :9001)<br/>[Telemetry Ingestion & Actuation]"]
        RMQ["🐇 RabbitMQ AMQP Broker (AMQP :5672 / Mgmt :15672)<br/>[Critical Alerts & Work Orders]"]
    end

    subgraph backend_core ["Backend Core (Spring Boot 4.1 / Java 21 - Clean Architecture)"]
        ING["📥 TelemetryMqttConsumer"]
        INGEST_UC["⚙️ ProcessTelemetryIngestionUseCase"]
        ANOMALY_UC["🔍 ProcessEquipmentAnomaliesUseCase"]
        EVAL_UC["⚡ EvaluateRuleGroupUseCase<br/>(Dynamic Rules Engine: AND / OR)"]
        ALERT_UC["🚨 TriggerAlertUseCase<br/>(Acknowledge / Dismiss Lifecycle)"]
        ACT_UC["🎛️ ExecuteActuationCommandUseCase<br/>(ActuationCommandPublisher Port)"]
        HVAC["🌡️ HVAC Thermal Controller Module"]
    end

    subgraph persistence ["Persistence & Storage Layer"]
        PG[("🐘 PostgreSQL 16<br/>(Config, EquipmentState, Alerts, Snapshots, Rules)")]
        TS[("⏱️ TimescaleDB Hypertable<br/>(telemetry_data)")]
        FLY["✈️ Flyway Migrations (V01–V19)"]
    end

    subgraph frontend_layer ["Web & Frontend Layer"]
        ANG["🅰️ Angular Real-Time Dashboard (WebSockets / SSE)"]
    end

    MS -->|"1. Publish industry/machinery/{equipCode}"| MQTT
    MQTT -->|"2. Consume Sensor Stream"| ING
    ING --> INGEST_UC
    INGEST_UC -->|"3. Save Time-Series Readings"| TS
    INGEST_UC -->|"4. Delegate Anomaly Check"| ANOMALY_UC
    ANOMALY_UC -->|"5. Evaluate Active RuleGroups"| EVAL_UC
    EVAL_UC -.->|"6. Trigger on Breach"| ALERT_UC
    ALERT_UC -->|"7. Persist Alert + Sensor Snapshots"| PG
    ANOMALY_UC -->|"8. Return Calculated HealthStatus"| INGEST_UC
    INGEST_UC -->|"9. Update O(1) EquipmentState"| PG
    FLY -.->|"Schema Versioning"| PG
    ALERT_UC -.->|"10. Publish Critical Event"| RMQ
    EVAL_UC -.->|"11. Closed-Loop AutomationRule"| ACT_UC
    EVAL_UC -.->|"12. Thermal Management Rule"| HVAC
    ACT_UC -->|"13. Publish Command to commandMqttTopic"| MQTT
    HVAC -->|"14. Actuation Command"| MQTT
    INGEST_UC -.->|"15. Real-Time Stream"| ANG
```

> 📖 **Detailed Workflow Diagrams:** For the step-by-step sequence diagram of telemetry ingestion, dynamic rule evaluation, and the alert lifecycle state machine, see [docs/telemetry-and-rules-flow.md](docs/telemetry-and-rules-flow.md).

---

## 🗺 Project Milestones & Current Progress

We follow an iterative milestone roadmap. Documented below is our current development status:

| Milestone | Scope & Objectives | Status |
| :--- | :--- | :---: |
| **Milestone 1** | **Infra & Ingestion Pipeline:** Docker Compose setup (TimescaleDB, Mosquitto, RabbitMQ) + Python Motor Simulator | COMPLETED ✅ |
| **Milestone 2** | **Database & Data Modeling:** TimescaleDB Hypertables, 3-Tier Schema (Config/State/History), Flyway Migrations | COMPLETED ✅ |
| **Milestone 3** | **Core Domain & Clean Architecture:** Pure Java Domain Entities, JPA Entities & Mappers, Rules Engine, Async Consumers | IN PROGRESS ⏳ |
| **Milestone 4** | **Frontend Integration & Real-Time:** Angular Dashboard, RxJS State, WebSockets / SSE streaming | PLANNED 📋 |
| **Milestone 5** | **Actuation & Feedback Loop:** Bi-directional MQTT commands (Emergency stop, speed throttling) | PLANNED 📋 |
| **Milestone 6** | **HVAC Control & Thermal Module:** Native internal HVAC thermal management engine | PLANNED 📋 |
| **Milestone 7** | **Enterprise Security & Observability:** Spring Security (JWT), Actuator metrics, Swagger / OpenAPI | PLANNED 📋 |

---

## ⚡ Tech Stack

- **Core Backend:** Java 21 LTS, Spring Boot 4.1.x, Spring Data JPA, Flyway DB
- **Messaging Brokers:** Eclipse Mosquitto v2 (MQTT / WebSockets), RabbitMQ 3.13 (AMQP)
- **Database:** PostgreSQL 16 + **TimescaleDB** (Timescale Hypertable time-series storage)
- **IoT Simulators:** Python 3.10+, Paho-MQTT 2.x
- **Frontend (Upcoming):** Angular, RxJS, Chart.js / ngx-charts
- **DevOps & Tools:** Docker, Docker Compose, Git, Linux Mint


---

## 📊 Telemetry Data Specification

The simulator models industrial 3-phase electric motors (e.g., 5CV, 380V operating under ISO 20816 vibration guidelines).

### MQTT Topic Format
`industry/machinery/{equipCode}`

### Sample Payload
```json
{
  "equipCode": "MTR-USI-001",
  "gatewayCode": "GW-USI-001",
  "timestamp": "2026-10-06T00:08:59.324289+00:00",
  "readings": [
    {
      "sensorCode": "VOL-001-MTR-USI-001",
      "value": 382.45
    },
    {
      "sensorCode": "CUR-001-MTR-USI-001",
      "value": 7.82
    },
    {
      "sensorCode": "TEM-001-MTR-USI-001",
      "value": 68.30
    },
    {
      "sensorCode": "VIB-001-MTR-USI-001",
      "value": 1.45
    }
  ]
}
```

---

## 🚀 Getting Started & Local Setup

### 1. Prerequisites
- [Docker & Docker Compose](https://docs.docker.com/get-docker/)
- [Java 21 JDK](https://adoptium.net/)
- [Python 3.10+](https://www.python.org/)

### 2. Clone the Repository
```bash
git clone https://github.com/igorhgds/industrial-iot-backend.git
cd industrial-iot-backend
```

### 3. Spin up Infrastructure Containers
Start PostgreSQL, Mosquitto MQTT, and RabbitMQ in background mode:
```bash
docker compose up -d
```

Verify service ports:
- **PostgreSQL:** `localhost:5432` (DB: `industrial_iiot_db`, User: `iiot_user`)
- **Mosquitto MQTT:** `localhost:1883` (MQTT), `localhost:9001` (WebSockets)
- **RabbitMQ:** `localhost:5672` (AMQP), `http://localhost:15672` (Management Console — User: `iiot` / Pass: `iiot`)

### 4. Run the Spring Boot Backend & Tests
```bash
# Run the application
./mvnw spring-boot:run

# Run the unit test suite
./mvnw test -Dtest="*UseCaseTest"
```

### 5. Run the Motor Telemetry Simulator
Set up a Python virtual environment and run the simulator:
```bash
cd simulators
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
python main.py
```
You will see real-time JSON telemetry payloads being published to Mosquitto MQTT topic `industry/machinery/MTR-USI-001` every 3 seconds.

---

## 📂 Repository Project Structure

```
industrial-iot-backend/
├── AGENTS.md                    # Architectural guidelines & developer quick commands
├── docker-compose.yml           # Local infrastructure stack (TimescaleDB, Mosquitto, RabbitMQ)
├── GITHUB_BACKLOG.md            # Detailed milestone tasks & issue definitions
├── docs/                        # Architecture & workflow documentation
│   ├── database-schemav1.dbml   # Database schema specification
│   └── telemetry-and-rules-flow.md # Detailed Mermaid flows (Ingestion, Rules, Alerts)
├── infra/                       # Infrastructure configuration files
│   └── mosquitto/
│       └── config/
│           └── mosquitto.conf   # Mosquitto listener & WebSocket settings
├── simulators/                  # Python IIoT telemetry simulators
│   ├── main.py                  # Entry point for telemetry publisher
│   ├── motor_simulator.py       # 5CV 380V Motor telemetry logic
│   └── requirements.txt         # Paho-MQTT dependencies
├── src/                         # Spring Boot application source code
│   ├── main/
│   │   ├── java/henrique/igor/iiot/
│   │   │   ├── application/usecases/ # Clean Architecture Use Cases & DTOs
│   │   │   ├── domain/               # Pure Java Entities, Enums, Exceptions, Ports & Repo Interfaces
│   │   │   └── infrastructure/       # Messaging (MQTT), Persistence (JPA Entities, Mappers, Adapters)
│   │   └── resources/
│   │       ├── application.yaml      # Spring Boot configurations
│   │       └── db/migration/         # Flyway SQL migration scripts (V01–V19)
│   └── test/                         # JUnit 5 & Mockito unit test suites
└── pom.xml                      # Maven dependencies & build setup
```

---

## 📜 Backlog & Sprint Tracking

For a complete breakdown of user stories, acceptance criteria, and issue specifications, refer to [GITHUB_BACKLOG.md](file:///media/igor/Projetos/00%20-%20Web%20Developer/01%20-%20Projetos/Industrial_IoT/industrial-iot-backend/GITHUB_BACKLOG.md) or visit our [GitHub Project Board](https://github.com/users/igorhgds/projects/5).

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
