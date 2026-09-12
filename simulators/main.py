from motor_simulator import MotorSimulator

if __name__ == "__main__":
   simulator = MotorSimulator(equipment_code="MTR-5CV-01", gateway_code="GW-01")
   simulator.start()