package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.PassportCityStats;
import com.stravamate.passport.domain.entity.PassportCountryStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PassportRepository {

    @Select("""
            SELECT NULLIF(country_code, '') AS country_code,
                   country_name,
                   COUNT(*)::int AS city_count,
                   COALESCE(SUM(activity_count), 0)::int AS activity_count,
                   COALESCE(SUM(total_distance_meters), 0) AS total_distance_meters,
                   MIN(first_activity_at) AS first_activity_at,
                   MAX(last_activity_at) AS last_activity_at,
                   AVG(representative_latitude) AS representative_latitude,
                   AVG(representative_longitude) AS representative_longitude
            FROM visited_places
            WHERE user_id = #{userId}
            GROUP BY NULLIF(country_code, ''), country_name
            ORDER BY COALESCE(SUM(total_distance_meters), 0) DESC,
                     COALESCE(SUM(activity_count), 0) DESC,
                     country_name ASC
            """)
    List<PassportCountryStats> findCountryStats(Long userId);

    @Select("""
            SELECT id AS visited_place_id,
                   NULLIF(country_code, '') AS country_code,
                   country_name,
                   NULLIF(region_name, '') AS region_name,
                   city_name,
                   activity_count,
                   total_distance_meters,
                   first_activity_at,
                   last_activity_at,
                   representative_latitude,
                   representative_longitude
            FROM visited_places
            WHERE user_id = #{userId}
            ORDER BY total_distance_meters DESC,
                     activity_count DESC,
                     country_name ASC,
                     city_name ASC
            """)
    List<PassportCityStats> findCityStats(Long userId);

    @Select("""
            SELECT id AS visited_place_id,
                   NULLIF(country_code, '') AS country_code,
                   country_name,
                   NULLIF(region_name, '') AS region_name,
                   city_name,
                   activity_count,
                   total_distance_meters,
                   first_activity_at,
                   last_activity_at,
                   representative_latitude,
                   representative_longitude
            FROM visited_places
            WHERE user_id = #{userId}
              AND representative_latitude IS NOT NULL
              AND representative_longitude IS NOT NULL
            ORDER BY country_name ASC,
                     city_name ASC
            """)
    List<PassportCityStats> findMapMarkerStats(Long userId);

    @Select("""
            SELECT id AS visited_place_id,
                   NULLIF(country_code, '') AS country_code,
                   country_name,
                   NULLIF(region_name, '') AS region_name,
                   city_name,
                   activity_count,
                   total_distance_meters,
                   first_activity_at,
                   last_activity_at,
                   representative_latitude,
                   representative_longitude
            FROM visited_places
            WHERE user_id = #{userId}
            ORDER BY last_activity_at DESC NULLS LAST,
                     total_distance_meters DESC,
                     city_name ASC
            LIMIT #{limit}
            """)
    List<PassportCityStats> findRecentPlaces(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
}
