# AGENTS.md — Industrial IIoT Backend

## Quick Commands

```bash
# Start infrastructure (PostgreSQL/TimescaleDB, Mosquitto MQTT, RabbitMQ)
docker compose up -d

# Run Spring Boot app (requires infra running)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build package
./mvnw package

# Run Python simulator (separate terminal)
cd simulators && python3 -m venv venv && source venv/bin/activate && pip install -r requirements.txt && python main.py
```

## Architecture Overview

- **Java 21**, **Spring Boot 4.1.x**, **Maven**
- **Clean/Hexagonal Architecture**: `domain/` (pure Java, no Spring), `infrastructure/` (JPA, adapters, mappers)
- **Dual-broker messaging**:
  - **Mosquitto MQTT** (port 1883/9001): telemetry ingestion + actuation commands
  - **RabbitMQ** (port 5672/15672): critical alerts & work orders (guaranteed delivery)
- **Three-tier data**:
  1. Config/Master: `equipments`, `sensors`, `actuators`, `gateways`, `rule_groups`, `rule_conditions`
  2. State/Operational: `equipment_state`, `alerts`, `alert_sensor_snapshots`, `work_orders`
  3. Historical: `telemetry_data` (TimescaleDB hypertable)
- **Flyway** migrations in `src/main/resources/db/migration/` (V1–V16+)
- **Python simulator** publishes to `industry/machinery/{equipmentCode}` every 3s


## Architecture Rules

- `domain` must not depend on Spring, JPA, MQTT, RabbitMQ or infrastructure.
- Infrastructure may depend on domain.
- Domain repository interfaces belong to the domain.
- Repository implementations belong to infrastructure.
- JPA annotations must not leak into domain entities.
- Business rules belong in domain/use-case layers, not controllers or persistence adapters.
- Infrastructure adapters translate external representations into domain representations.


## Key Conventions

- **Domain entities**: pure Java records/classes, no Spring/JPA annotations, immutable where possible
- **Repositories**: interfaces in `domain/repositories/`, implementations in `infrastructure/persistence/adapters/`
- **JPA entities**: in `infrastructure/persistence/entities/`, suffixed `JpaEntity`
- **Mappers**: static utility classes in `infrastructure/persistence/mappers/`
- **Enums**: in `domain/entities/enums/`
- **Virtual threads enabled**: `spring.threads.virtual.enabled=true`
- **DDL mode**: `validate` (Flyway owns schema)

## Database & Migrations

- **TimescaleDB** (PostgreSQL 16 + Timescale extension)
- Flyway runs automatically on startup
- Migration naming: `V{number}__{description}.sql`
- Never edit applied migrations; create new ones
- Connect: `jdbc:postgresql://localhost:5432/industrial_iiot_db`
- User/password: see local `application.yaml` or environment variables

## Testing

- Only one test exists: context load (`IndustrialIotBackendApplicationTests`)
- Test dependencies: JUnit 5, Mockito, Spring Boot test starters, Testcontainers (not yet used)
- Run single test: `./mvnw test -Dtest=IndustrialIotBackendApplicationTests`

## Simulator

- Entry: `simulators/main.py` → `MotorSimulator`
- Payload matches expected DTO: `equipmentCode`, `gatewayCode`, `timestamp`, `readings[]` with `sensorCode`/`value`
- Topic: `industry/machinery/{equipmentCode}` (QoS 1)
- Requires Mosquitto running on `localhost:1883`

## Gotchas

- `application.yaml` has hardcoded dev credentials; not for production
- SpringDoc/OpenAPI disabled (`enabled: false`)
- No security, no actuator endpoints exposed yet
- Lombok used (annotation processor configured in `pom.xml`)
- Python simulator uses Paho MQTT 1.6.1 (API v2 with fallback)