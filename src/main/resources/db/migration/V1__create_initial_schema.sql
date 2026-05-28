CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    strava_athlete_id BIGINT NOT NULL,
    username VARCHAR(100),
    firstname VARCHAR(100),
    lastname VARCHAR(100),
    profile_image_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_synced_at TIMESTAMPTZ,
    CONSTRAINT uq_users_strava_athlete_id UNIQUE (strava_athlete_id)
);

CREATE TABLE strava_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    scope TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_strava_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_strava_tokens_user_id UNIQUE (user_id)
);

CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    strava_activity_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    sport_type VARCHAR(50),
    start_date TIMESTAMPTZ NOT NULL,
    start_date_local TIMESTAMP,
    timezone VARCHAR(100),
    distance_meters NUMERIC(12,2) NOT NULL DEFAULT 0,
    moving_time_seconds INTEGER,
    elapsed_time_seconds INTEGER,
    start_latitude NUMERIC(9,6),
    start_longitude NUMERIC(9,6),
    raw_start_latlng JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_activities_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_activities_user_strava_activity UNIQUE (user_id, strava_activity_id)
);

CREATE TABLE visited_places (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    country_code VARCHAR(10),
    country_name VARCHAR(100) NOT NULL,
    region_name VARCHAR(100),
    city_name VARCHAR(100) NOT NULL,
    representative_latitude NUMERIC(9,6),
    representative_longitude NUMERIC(9,6),
    first_activity_at TIMESTAMPTZ,
    last_activity_at TIMESTAMPTZ,
    activity_count INTEGER NOT NULL DEFAULT 0,
    total_distance_meters NUMERIC(14,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_visited_places_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_visited_places_user_location UNIQUE (user_id, country_code, region_name, city_name)
);

CREATE TABLE activity_places (
    id BIGSERIAL PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    visited_place_id BIGINT NOT NULL,
    source VARCHAR(50) NOT NULL DEFAULT 'START_LATLNG',
    matched_latitude NUMERIC(9,6),
    matched_longitude NUMERIC(9,6),
    distance_meters NUMERIC(12,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_activity_places_activity FOREIGN KEY (activity_id) REFERENCES activities (id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_places_visited_place FOREIGN KEY (visited_place_id) REFERENCES visited_places (id) ON DELETE CASCADE,
    CONSTRAINT uq_activity_places_activity_place_source UNIQUE (activity_id, visited_place_id, source),
    CONSTRAINT ck_activity_places_source CHECK (source IN ('START_LATLNG', 'POLYLINE', 'STREAMS'))
);

CREATE TABLE sync_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sync_type VARCHAR(50) NOT NULL DEFAULT 'ACTIVITIES',
    status VARCHAR(50) NOT NULL,
    requested_count INTEGER NOT NULL DEFAULT 0,
    synced_count INTEGER NOT NULL DEFAULT 0,
    skipped_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMPTZ,
    CONSTRAINT fk_sync_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_sync_logs_sync_type CHECK (sync_type IN ('ACTIVITIES')),
    CONSTRAINT ck_sync_logs_status CHECK (status IN ('STARTED', 'SUCCESS', 'FAILED', 'PARTIAL_SUCCESS'))
);

CREATE INDEX idx_activities_user_start_date ON activities (user_id, start_date DESC);
CREATE INDEX idx_activities_user_type ON activities (user_id, type, sport_type);
CREATE INDEX idx_activities_start_latlng ON activities (start_latitude, start_longitude);
CREATE INDEX idx_visited_places_user_country ON visited_places (user_id, country_code);
CREATE INDEX idx_visited_places_user_city ON visited_places (user_id, city_name);
CREATE INDEX idx_visited_places_user_activity_count ON visited_places (user_id, activity_count DESC);
CREATE INDEX idx_activity_places_place ON activity_places (visited_place_id);
CREATE INDEX idx_sync_logs_user_started_at ON sync_logs (user_id, started_at DESC);
