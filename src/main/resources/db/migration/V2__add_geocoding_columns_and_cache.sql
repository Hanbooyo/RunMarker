ALTER TABLE activities
ADD COLUMN city_name VARCHAR(100),
ADD COLUMN region_name VARCHAR(100),
ADD COLUMN country_name VARCHAR(100),
ADD COLUMN country_code VARCHAR(10),
ADD COLUMN geocoded_at TIMESTAMPTZ;

CREATE INDEX idx_activities_user_country_city
ON activities (user_id, country_code, city_name);

CREATE TABLE geocoding_cache (
    id BIGSERIAL PRIMARY KEY,
    cache_key VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    rounded_latitude NUMERIC(9,6) NOT NULL,
    rounded_longitude NUMERIC(9,6) NOT NULL,
    city_name VARCHAR(100),
    region_name VARCHAR(100),
    country_name VARCHAR(100) NOT NULL,
    country_code VARCHAR(10),
    raw_response JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_geocoding_cache_key_provider UNIQUE (cache_key, provider)
);

CREATE INDEX idx_geocoding_cache_key
ON geocoding_cache (cache_key, provider);
