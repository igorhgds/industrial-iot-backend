from motor_simulator import MotorSimulator

if __name__ == "__main__":
    simulator = MotorSimulator(equip_code="MTR-USI-001", gateway_code="GW-USI-001")
    simulator.start()