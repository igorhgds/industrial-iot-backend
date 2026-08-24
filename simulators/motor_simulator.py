import time
from datetime import datetime
import json
import random
import paho.mqtt.client as mqtt

class MotorSimulator:
   def __init__(self, equipment_code, mqtt_broker="localhost", mqtt_port=1883):
        self.equipment_code = equipment_code
        self.mqtt_broker = mqtt_broker
        self.mqtt_port = mqtt_port

        # Paho MQTT 2.x support with CallbackAPIVersion
        try:
            self.client = mqtt.Client(
                callback_api_version=mqtt.CallbackAPIVersion.VERSION2,
                client_id=f"motor-{equipment_code}"
            )
        except AttributeError:
            # Backward compatibility for Paho MQTT 1.x
            self.client = mqtt.Client(client_id=f"motor-{equipment_code}")

        self.client.on_connect = self.on_connect
        self.topic = f"industry/machinery/{equipment_code}"

   def on_connect(self, client, userdata, flags, rc, properties=None):
        if rc == 0:
            print(f"[MQTT] Connected successfully to {self.mqtt_broker}:{self.mqtt_port}")
        else:
            print(f"[MQTT] Connection failed with response code {rc}")

   #para um motor 5CV, 380V
   def generate_telemetry(self):
      return {
            "equipmentCode": self.equipment_code,  # camelCase para bater com o Java DTO
            "timestamp": datetime.now().isoformat(),  # Formato ISO-8601 (ex: "2026-08-17T14:30:00.123456")
            "readings": [
                {
                    "sensorCode": "VOLTAGE",
                    "value": round(random.uniform(360, 400), 2)
                },
                {
                    "sensorCode": "CURRENT",
                    "value": round(random.uniform(6, 9.5), 2)
                },
                {
                    "sensorCode": "TEMPERATURE",
                    "value": round(random.uniform(40, 95), 2)
                },
                {
                    "sensorCode": "VIBRATION",
                    "value": round(random.uniform(0.1, 4.7), 2)  # Vibração RMS (ISO 20816)
                }
            ]
        }

   def start(self):
      self.client.connect(self.mqtt_broker, self.mqtt_port, 60) #keepalive=60
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

   