ALTER TABLE sensors
    DROP COLUMN IF EXISTS min_threshold,
    DROP COLUMN IF EXISTS max_threshold;