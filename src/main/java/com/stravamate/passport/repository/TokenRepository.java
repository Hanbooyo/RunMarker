package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.StravaToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface TokenRepository {

    @Select("""
            SELECT id, user_id, access_token, refresh_token, expires_at, scope, created_at, updated_at
            FROM strava_tokens
            WHERE user_id = #{userId}
            """)
    Optional<StravaToken> findByUserId(Long userId);

    @Select("""
            INSERT INTO strava_tokens (
                user_id, access_token, refresh_token, expires_at, scope,
                created_at, updated_at
            )
            VALUES (
                #{userId}, #{accessToken}, #{refreshToken}, #{expiresAt}, #{scope},
                NOW(), NOW()
            )
            ON CONFLICT (user_id)
            DO UPDATE SET
                access_token = EXCLUDED.access_token,
                refresh_token = EXCLUDED.refresh_token,
                expires_at = EXCLUDED.expires_at,
                scope = EXCLUDED.scope,
                updated_at = NOW()
            RETURNING id, user_id, access_token, refresh_token, expires_at, scope, created_at, updated_at
            """)
    StravaToken upsert(StravaToken token);
}
