package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.VisitedPlace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.Instant;

@Mapper
public interface VisitedPlaceRepository {

    @Select("""
            INSERT INTO visited_places (
                user_id, country_code, country_name, region_name, city_name,
                representative_latitude, representative_longitude,
                activity_count, total_distance_meters,
                created_at, updated_at
            )
            VALUES (
                #{userId}, #{countryCode}, #{countryName}, #{regionName}, #{cityName},
                #{representativeLatitude}, #{representativeLongitude},
                0, 0,
                NOW(), NOW()
            )
            ON CONFLICT (user_id, country_code, region_name, city_name)
            DO UPDATE SET
                country_name = EXCLUDED.country_name,
                representative_latitude = COALESCE(visited_places.representative_latitude, EXCLUDED.representative_latitude),
                representative_longitude = COALESCE(visited_places.representative_longitude, EXCLUDED.representative_longitude),
                updated_at = NOW()
            RETURNING id, user_id, country_code, country_name, region_name, city_name,
                      representative_latitude, representative_longitude,
                      first_activity_at, last_activity_at, activity_count, total_distance_meters,
                      created_at, updated_at
            """)
    VisitedPlace upsertIdentity(VisitedPlace visitedPlace);

    @Update("""
            UPDATE visited_places
            SET first_activity_at = CASE
                    WHEN first_activity_at IS NULL OR first_activity_at > #{activityStartedAt}
                    THEN #{activityStartedAt}
                    ELSE first_activity_at
                END,
                last_activity_at = CASE
                    WHEN last_activity_at IS NULL OR last_activity_at < #{activityStartedAt}
                    THEN #{activityStartedAt}
                    ELSE last_activity_at
                END,
                activity_count = activity_count + 1,
                total_distance_meters = total_distance_meters + #{distanceMeters},
                updated_at = NOW()
            WHERE id = #{visitedPlaceId}
            """)
    int incrementStats(
            @Param("visitedPlaceId") Long visitedPlaceId,
            @Param("activityStartedAt") Instant activityStartedAt,
            @Param("distanceMeters") BigDecimal distanceMeters
    );
}
