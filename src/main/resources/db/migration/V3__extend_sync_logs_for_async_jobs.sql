ALTER TABLE sync_logs
    ADD COLUMN mode VARCHAR(20) NOT NULL DEFAULT 'recent',
    ADD COLUMN geocoded_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN geocoding_failed_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN rate_limit_limit VARCHAR(50),
    ADD COLUMN rate_limit_usage VARCHAR(50);

CREATE INDEX idx_sync_logs_user_status ON sync_logs (user_id, status);
