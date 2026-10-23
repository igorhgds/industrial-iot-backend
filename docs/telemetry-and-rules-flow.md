# Fluxo de Ingestão de Telemetria, Motor de Regras, Alertas e Atuação

Este documento detalha o fluxo completo de ponta a ponta (End-to-End) desde a chegada do pacote MQTT enviado pelo simulador/equipamento até a avaliação de regras dinâmicas, geração de alertas e disparo de comandos para atuadores.

---

## 1. Em que momento o `EvaluateRuleGroupUseCase` é acionado?

O ponto de encontro entre os dados que chegam do MQTT e o motor de regras é **dentro (ou logo após) do `ProcessTelemetryIngestionUseCase`**.

Repare no que o `ProcessTelemetryIngestionUseCase` já faz hoje nas linhas 43–66:
1. Ele percorre cada leitura (`ReadingDTO`) do pacote MQTT, busca o `Sensor` e salva no TimescaleDB (`telemetry_data`).
2. Logo em seguida, ele atualiza a tabela `equipment_state` definindo o `HealthStatus` (`HEALTH`, `WARNING`, `CRITICAL`).

**É exatamente entre o passo 1 e o passo 2 que o `EvaluateRuleGroupUseCase` entra!**
Quando o loop de leituras termina, nós já temos em mãos:
- O `Equipment` (`equipmentId`)
- Todos os valores recém-lidos mapeados por sensor: `Map<UUID, BigDecimal> sensorReadings`

Nesse exato instante:
1. Buscamos todos os grupos de regras daquele equipamento (`ruleGroupRepository.findByEquipmentId(equipmentId)`).
2. Para cada `RuleGroup` ativo, chamamos o **`EvaluateRuleGroupUseCase.execute(...)`** passando o `Map<UUID, BigDecimal>` que acabou de chegar no payload MQTT.
3. **Se `result.triggered() == false`**: o equipamento continua saudável (`HealthStatus.HEALTH`).
4. **Se `result.triggered() == true`**:
   - O status de saúde do equipamento (`equipment_state`) passa para `WARNING` ou `CRITICAL` (conforme `result.calculatedSeverity()`).
   - Chamamos o **`TriggerAlertUseCase`** para gravar o `Alert` + `AlertSensorSnapshot` no banco.
   - Se houver uma `AutomationRule` vinculada à condição violada, chamamos o **`ExecuteActuationCommandUseCase`** para enviar o comando ao atuador via MQTT.

---

## 2. Diagrama de Fluxo Geral (Flowchart)

```mermaid
flowchart TD
    Sim["Simulador Python / CLP<br/>(Publica a cada 3s)"] -->|"MQTT: industry/machinery/{equipCode}"| Broker["Mosquitto MQTT Broker"]
    Broker --> Consumer["TelemetryMqttConsumer<br/>(handleMessage)"]
    Consumer -->|"TelemetryPayloadDTO + rawPayload"| Ingest["ProcessTelemetryIngestionUseCase"]

    subgraph Ingestion ["1. Validação e Persistência Histórica"]
        Ingest --> ValEquip["Valida Equipment e Gateway"]
        ValEquip --> LoopSensors["Para cada leitura (ReadingDTO):<br/>1. Busca Sensor<br/>2. Salva TelemetryData (TimescaleDB)<br/>3. Monta Map&lt;sensorId, valor&gt;"]
    end

    LoopSensors --> FetchRules["Busca RuleGroups do Equipamento"]

    subgraph RulesEngine ["2. Dynamic Rules Engine"]
        FetchRules --> Eval["EvaluateRuleGroupUseCase.execute()<br/>(Compara Map&lt;sensorId, valor&gt; com RuleConditions)"]
        Eval --> CheckOp{"Violou condições?<br/>(AND / OR)"}
    end

    CheckOp -->|"Não (triggered = false)"| Healthy["Atualiza EquipmentState<br/>status = HEALTH"]

    CheckOp -->|"Sim (triggered = true)"| Unhealthy["Atualiza EquipmentState<br/>status = WARNING / CRITICAL"]

    subgraph AlertAndActuation ["3. Gestão de Alertas e Atuação (Closed-Loop)"]
        Unhealthy --> TriggerAlert["TriggerAlertUseCase.execute()<br/>Salva Alert (PENDING) + AlertSensorSnapshots"]
        TriggerAlert --> CheckAuto{"Possui AutomationRule<br/>ativa para a condição?"}
        CheckAuto -->|"Sim"| ExecAct["ExecuteActuationCommandUseCase.execute()<br/>Publica comando no tópico MQTT do Atuador"]
        CheckAuto -->|"Não"| EndFlow["Fim do Processamento"]
        ExecAct --> BrokerCmd["Mosquitto MQTT / RabbitMQ"]
    end
```

---

## 3. Diagrama de Sequência Detalhado (Sequence Diagram)

```mermaid
sequenceDiagram
    autonumber
    participant MQTT as Mosquitto MQTT
    participant Consumer as TelemetryMqttConsumer
    participant IngestUC as ProcessTelemetryIngestionUseCase
    participant EvalUC as EvaluateRuleGroupUseCase
    participant AlertUC as TriggerAlertUseCase
    participant AutoUC as ExecuteActuationCommandUseCase
    participant DB as PostgreSQL / TimescaleDB

    MQTT->>Consumer: Mensagem JSON (equipCode, gatewayCode, readings[])
    Consumer->>IngestUC: execute(payloadDTO, rawPayload)

    IngestUC->>DB: Valida Equipment + Gateway
    loop Para cada ReadingDTO no payload
        IngestUC->>DB: Busca Sensor por código
        IngestUC->>DB: Salva TelemetryData (Série Temporal)
        Note over IngestUC: Guarda (sensorId -> valor) no Map
    end

    IngestUC->>DB: Busca RuleGroups do Equipment
    loop Para cada RuleGroup do Equipamento
        IngestUC->>EvalUC: execute(ruleGroupId, Map<sensorId, valor>)
        EvalUC->>DB: Busca RuleConditions do RuleGroup
        Note over EvalUC: Avalia operadores (>, <, =, >=, <=)<br/>e aplica lógica AND / OR
        EvalUC-->>IngestUC: RuleEvaluationResult (triggered, severity, snapshots)

        alt Se triggered == true (Anomalia Detectada)
            IngestUC->>AlertUC: execute(TriggerAlertInput)
            AlertUC->>DB: Salva Alert (PENDING) + AlertSensorSnapshots
            AlertUC-->>IngestUC: Alert criado

            opt Se houver AutomationRule ativa para a condição violada
                IngestUC->>AutoUC: execute(ExecuteActuationCommandInput)
                AutoUC->>MQTT: Publica comando no commandMqttTopic do Atuador
            end
        end
    end

    IngestUC->>DB: Atualiza EquipmentState (HEALTH, WARNING ou CRITICAL)
```

---

## 4. Ciclo de Vida do Alerta (Operador Humano)

Depois que o `TriggerAlertUseCase` cria o alerta com status `PENDING`, o operador interage com ele via API REST:

```mermaid
stateDiagram-v2
    [*] --> PENDING: TriggerAlertUseCase (Automático via Telemetria)
    PENDING --> ACKNOWLEDGED: AcknowledgeAlertUseCase (Operador reconhece)
    PENDING --> RESOLVED: DismissAlertUseCase (Resolvido diretamente)
    ACKNOWLEDGED --> RESOLVED: DismissAlertUseCase (Operador encerra alerta)
    RESOLVED --> [*]
```
