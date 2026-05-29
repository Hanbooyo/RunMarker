ALTER TABLE sync_logs
    ADD COLUMN progress_updated_at TIMESTAMPTZ;

UPDATE sync_logs
SET progress_updated_at = started_at
WHERE progress_updated_at IS NULL;

CREATE INDEX idx_sync_logs_running_heartbeat
    ON sync_logs (user_id, mode, status, progress_updated_at DESC);
