import time
from datetime import datetime, timezone
import json
import random
import paho.mqtt.client as mqtt

class MotorSimulator:
    def __init__(self, equip_code="MTR-USI-001", gateway_code="GW-USI-001", mqtt_broker="localhost", mqtt_port=1883):
        self.equipment_code = equip_code
        self.gateway_code = gateway_code
        self.mqtt_broker = mqtt_broker
        self.mqtt_port = mqtt_port

        # Paho MQTT 2.x support with CallbackAPIVersion
        try:
            self.client = mqtt.Client(
                callback_api_version=mqtt.CallbackAPIVersion.VERSION2,
                client_id=f"motor-{equip_code}"
            )
        except AttributeError:
            # Backward compatibility for Paho MQTT 1.x
            self.client = mqtt.Client(client_id=f"motor-{equip_code}")

        self.client.on_connect = self.on_connect
        self.topic = f"industry/machinery/{equip_code}"

    def on_connect(self, client, userdata, flags, rc, properties=None):
        if rc == 0:
            print(f"[MQTT] Connected successfully to {self.mqtt_broker}:{self.mqtt_port}")
        else:
            print(f"[MQTT] Connection failed with response code {rc}")

    # Para um motor industrial 5CV, 380V (Padrão ISO 20816)
    def generate_telemetry(self):
        return {
            "equipCode": self.equipment_code,      # Alinhado com Java DTO (equipCode) e EquipmentCodeGenerator (ex: MTR-USI-001)
            "gatewayCode": self.gateway_code,      # Alinhado com GatewayCodeGenerator (ex: GW-USI-001)
            "timestamp": datetime.now(timezone.utc).isoformat(),  # Formato ISO-8601 com Fuso UTC (+00:00)
            "readings": [
                {
                    "sensorCode": f"VOL-001-{self.equipment_code}",  # Alinhado com SensorCodeGenerator (VOL-001-MTR-USI-001)
                    "value": round(random.uniform(360, 400), 2)
                },
                {
                    "sensorCode": f"CUR-001-{self.equipment_code}",  # Alinhado com SensorCodeGenerator (CUR-001-MTR-USI-001)
                    "value": round(random.uniform(6, 9.5), 2)
                },
                {
                    "sensorCode": f"TEM-001-{self.equipment_code}",  # Alinhado com SensorCodeGenerator (TEM-001-MTR-USI-001)
                    "value": round(random.uniform(40, 95), 2)
                },
                {
                    "sensorCode": f"VIB-001-{self.equipment_code}",  # Alinhado com SensorCodeGenerator (VIB-001-MTR-USI-001)
                    "value": round(random.uniform(0.1, 4.7), 2)  # Vibração RMS (ISO 20816)
                }
            ]
        }

    def start(self):
        self.client.connect(self.mqtt_broker, self.mqtt_port, 60)  # keepalive=60
        self.client.loop_start()

        print(f"[MotorSimulator] Starting telemetry for equipment {self.equipment_code}...")

        try:
            while True:
                telemetry = self.generate_telemetry()
                payload = json.dumps(telemetry)

                self.client.publish(self.topic, payload, qos=1)
                print(f"[PUBLISHED to {self.topic}] {payload}")

                time.sleep(3)  # Publish telemetry every 3 seconds

        except KeyboardInterrupt:
            print("[MotorSimulator] Stopping telemetry...")
            self.client.loop_stop()
            self.client.disconnect()
            print("[MotorSimulator] Disconnected.")

if __name__ == "__main__":
    simulator = MotorSimulator()
    simulator.start()