package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.GeocodingCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface GeocodingCacheRepository {

    @Select("""
            SELECT id, cache_key, provider, rounded_latitude, rounded_longitude,
                   city_name, region_name, country_name, country_code,
                   raw_response::text AS raw_response,
                   created_at, updated_at
            FROM geocoding_cache
            WHERE cache_key = #{cacheKey}
              AND provider = #{provider}
            """)
    Optional<GeocodingCache> findByCacheKeyAndProvider(
            @Param("cacheKey") String cacheKey,
            @Param("provider") String provider
    );

    @Select("""
            INSERT INTO geocoding_cache (
                cache_key, provider, rounded_latitude, rounded_longitude,
                city_name, region_name, country_name, country_code, raw_response,
                created_at, updated_at
            )
            VALUES (
                #{cacheKey}, #{provider}, #{roundedLatitude}, #{roundedLongitude},
                #{cityName}, #{regionName}, #{countryName}, #{countryCode},
                CAST(#{rawResponse} AS jsonb),
                NOW(), NOW()
            )
            ON CONFLICT (cache_key, provider)
            DO UPDATE SET
                city_name = EXCLUDED.city_name,
                region_name = EXCLUDED.region_name,
                country_name = EXCLUDED.country_name,
                country_code = EXCLUDED.country_code,
                raw_response = EXCLUDED.raw_response,
                updated_at = NOW()
            RETURNING id, cache_key, provider, rounded_latitude, rounded_longitude,
                      city_name, region_name, country_name, country_code,
                      raw_response::text AS raw_response,
                      created_at, updated_at
            """)
    GeocodingCache upsert(GeocodingCache cache);
}
