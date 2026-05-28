package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.ActivityPlace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface ActivityPlaceRepository {

    @Select("""
            INSERT INTO activity_places (
                activity_id, visited_place_id, source,
                matched_latitude, matched_longitude, distance_meters,
                created_at
            )
            VALUES (
                #{activityId}, #{visitedPlaceId}, #{source},
                #{matchedLatitude}, #{matchedLongitude}, #{distanceMeters},
                NOW()
            )
            ON CONFLICT (activity_id, visited_place_id, source) DO NOTHING
            RETURNING id, activity_id, visited_place_id, source,
                      matched_latitude, matched_longitude, distance_meters, created_at
            """)
    Optional<ActivityPlace> insertIfNotExists(ActivityPlace activityPlace);
}
