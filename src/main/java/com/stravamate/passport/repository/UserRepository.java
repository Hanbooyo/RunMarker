package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

@Mapper
public interface UserRepository {

    @Select("""
            SELECT id, strava_athlete_id, username, firstname, lastname, profile_image_url,
                   created_at, updated_at, last_synced_at
            FROM users
            WHERE id = #{id}
            """)
    Optional<User> findById(Long id);

    @Select("""
            SELECT id, strava_athlete_id, username, firstname, lastname, profile_image_url,
                   created_at, updated_at, last_synced_at
            FROM users
            WHERE strava_athlete_id = #{stravaAthleteId}
            """)
    Optional<User> findByStravaAthleteId(Long stravaAthleteId);

    @Select("""
            INSERT INTO users (
                strava_athlete_id, username, firstname, lastname, profile_image_url,
                created_at, updated_at
            )
            VALUES (
                #{stravaAthleteId}, #{username}, #{firstname}, #{lastname}, #{profileImageUrl},
                NOW(), NOW()
            )
            ON CONFLICT (strava_athlete_id)
            DO UPDATE SET
                username = EXCLUDED.username,
                firstname = EXCLUDED.firstname,
                lastname = EXCLUDED.lastname,
                profile_image_url = EXCLUDED.profile_image_url,
                updated_at = NOW()
            RETURNING id, strava_athlete_id, username, firstname, lastname, profile_image_url,
                      created_at, updated_at, last_synced_at
            """)
    User upsert(User user);

    @Update("""
            UPDATE users
            SET last_synced_at = NOW(),
                updated_at = NOW()
            WHERE id = #{userId}
            """)
    int updateLastSyncedAt(Long userId);
}
