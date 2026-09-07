CREATE TABLE IF NOT EXISTS gateways (
    gateway_id       UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    code             VARCHAR(50)     NOT NULL UNIQUE, -- Ex: "GW-SECTOR-A-01"
    mac_address      VARCHAR(17)     UNIQUE,
    ip_address       VARCHAR(45),
    firmware_version VARCHAR(20),
    status           VARCHAR(30)     NOT NULL DEFAULT 'ONLINE', -- ONLINE, OFFLINE, MAINTENANCE
    sector_id        UUID,
    last_ping        TIMESTAMPTZ,
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_gateway_sector
         FOREIGN KEY (sector_id) REFERENCES sectors(sector_id) ON DELETE SET NULL
);

ALTER TABLE equipments
    ADD COLUMN IF NOT EXISTS gateway_id UUID REFERENCES gateways(gateway_id);