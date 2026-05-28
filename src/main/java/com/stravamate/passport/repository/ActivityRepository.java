package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ActivityRepository {

    @Select("""
            INSERT INTO activities (
                user_id, strava_activity_id, name, type, sport_type,
                start_date, start_date_local, timezone,
                distance_meters, moving_time_seconds, elapsed_time_seconds,
                start_latitude, start_longitude,
                created_at, updated_at
            )
            VALUES (
                #{userId}, #{stravaActivityId}, #{name}, #{type}, #{sportType},
                #{startDate}, #{startDateLocal}, #{timezone},
                #{distanceMeters}, #{movingTimeSeconds}, #{elapsedTimeSeconds},
                #{startLatitude}, #{startLongitude},
                NOW(), NOW()
            )
            ON CONFLICT (user_id, strava_activity_id) DO NOTHING
            RETURNING id, user_id, strava_activity_id, name, type, sport_type,
                      start_date, start_date_local, timezone,
                      distance_meters, moving_time_seconds, elapsed_time_seconds,
                      start_latitude, start_longitude,
                      city_name, region_name, country_name, country_code, geocoded_at,
                      created_at, updated_at
            """)
    Optional<Activity> insertIfNotExists(Activity activity);

    @Select("""
            SELECT id, user_id, strava_activity_id, name, type, sport_type,
                   start_date, start_date_local, timezone,
                   distance_meters, moving_time_seconds, elapsed_time_seconds,
                   start_latitude, start_longitude,
                   city_name, region_name, country_name, country_code, geocoded_at,
                   created_at, updated_at
            FROM activities
            WHERE user_id = #{userId}
            ORDER BY start_date DESC
            LIMIT #{limit}
            OFFSET #{offset}
            """)
    List<Activity> findAllByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("""
            SELECT id, user_id, strava_activity_id, name, type, sport_type,
                   start_date, start_date_local, timezone,
                   distance_meters, moving_time_seconds, elapsed_time_seconds,
                   start_latitude, start_longitude,
                   city_name, region_name, country_name, country_code, geocoded_at,
                   created_at, updated_at
            FROM activities
            WHERE user_id = #{userId}
              AND id = #{id}
            """)
    Optional<Activity> findByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    @Select("""
            SELECT id, user_id, strava_activity_id, name, type, sport_type,
                   start_date, start_date_local, timezone,
                   distance_meters, moving_time_seconds, elapsed_time_seconds,
                   start_latitude, start_longitude,
                   city_name, region_name, country_name, country_code, geocoded_at,
                   created_at, updated_at
            FROM activities
            WHERE user_id = #{userId}
              AND strava_activity_id = #{stravaActivityId}
            """)
    Optional<Activity> findByUserIdAndStravaActivityId(
            @Param("userId") Long userId,
            @Param("stravaActivityId") Long stravaActivityId
    );

    @Update("""
            UPDATE activities
            SET city_name = #{cityName},
                region_name = #{regionName},
                country_name = #{countryName},
                country_code = #{countryCode},
                geocoded_at = NOW(),
                updated_at = NOW()
            WHERE id = #{activityId}
            """)
    int updateGeocoding(
            @Param("activityId") Long activityId,
            @Param("cityName") String cityName,
            @Param("regionName") String regionName,
            @Param("countryName") String countryName,
            @Param("countryCode") String countryCode
    );
}
