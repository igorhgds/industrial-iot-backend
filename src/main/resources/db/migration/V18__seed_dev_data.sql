-- Flyway Seed Data for Local Development (Motor Telemetry Simulator)

-- 1. Insert Sector
INSERT INTO sectors (sector_id, name, description)
VALUES ('a1b2c3d4-e5f6-7890-1234-56789abcdef0', 'Usinagem', 'Setor de usinagem e torneamento industrial')
ON CONFLICT DO NOTHING;

-- 2. Insert Gateway
INSERT INTO gateways (gateway_id, code, mac_address, ip_address, firmware_version, status, sector_id)
VALUES (
    'b2c3d4e5-f6a7-8901-2345-6789abcdef01',
    'GW-USI-001',
    'AA:BB:CC:DD:EE:01',
    '192.168.1.100',
    'v1.0.0',
    'ONLINE',
    'a1b2c3d4-e5f6-7890-1234-56789abcdef0'
)
ON CONFLICT (code) DO NOTHING;

-- 3. Insert Equipment
INSERT INTO equipments (equipment_id, equip_code, type, status, sector_id, gateway_id)
VALUES (
    'c3d4e5f6-a7b8-9012-3456-789abcdef012',
    'MTR-USI-001',
    'MOTOR',
    'ACTIVE',
    'a1b2c3d4-e5f6-7890-1234-56789abcdef0',
    'b2c3d4e5-f6a7-8901-2345-6789abcdef01'
)
ON CONFLICT (equip_code) DO NOTHING;

-- 4. Insert Sensors for MTR-USI-001
INSERT INTO sensors (sensor_id, equipment_id, code, sensor_type, unit_of_measure, status, mqtt_topic)
VALUES
    ('d4e5f6a7-b8c9-0123-4567-89abcdef0123', 'c3d4e5f6-a7b8-9012-3456-789abcdef012', 'VOL-001-MTR-USI-001', 'VOLTAGE', 'V', 'ONLINE', 'industry/machinery/MTR-USI-001'),
    ('e5f6a7b8-c9d0-1234-5678-9abcdef01234', 'c3d4e5f6-a7b8-9012-3456-789abcdef012', 'CUR-001-MTR-USI-001', 'CURRENT', 'A', 'ONLINE', 'industry/machinery/MTR-USI-001'),
    ('f6a7b8c9-d0e1-2345-6789-abcdef012345', 'c3d4e5f6-a7b8-9012-3456-789abcdef012', 'TEM-001-MTR-USI-001', 'TEMPERATURE', 'C', 'ONLINE', 'industry/machinery/MTR-USI-001'),
    ('a7b8c9d0-e1f2-3456-789a-bcdef0123456', 'c3d4e5f6-a7b8-9012-3456-789abcdef012', 'VIB-001-MTR-USI-001', 'VIBRATION', 'mm/s', 'ONLINE', 'industry/machinery/MTR-USI-001')
ON CONFLICT DO NOTHING;
