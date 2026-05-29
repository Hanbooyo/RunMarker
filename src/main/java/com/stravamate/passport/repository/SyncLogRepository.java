package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.SyncLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

@Mapper
public interface SyncLogRepository {

    @Select("""
            INSERT INTO sync_logs (
                user_id, sync_type, mode, status, requested_count, synced_count,
                geocoded_count, geocoding_failed_count, skipped_count,
                started_at, progress_updated_at
            )
            VALUES (
                #{userId}, 'ACTIVITIES', #{mode}, 'STARTED', 0, 0, 0, 0, 0,
                NOW(), NOW()
            )
            RETURNING id, user_id, sync_type, status, requested_count, synced_count,
                      geocoded_count, geocoding_failed_count, skipped_count,
                      rate_limit_limit, rate_limit_usage, error_message, started_at,
                      progress_updated_at, finished_at, mode
            """)
    SyncLog insertStarted(@Param("userId") Long userId, @Param("mode") String mode);

    @Select("""
            SELECT id, user_id, sync_type, mode, status, requested_count, synced_count,
                   geocoded_count, geocoding_failed_count, skipped_count,
                   rate_limit_limit, rate_limit_usage, error_message, started_at,
                   progress_updated_at, finished_at
            FROM sync_logs
            WHERE id = #{id}
              AND user_id = #{userId}
            """)
    Optional<SyncLog> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("""
            SELECT id, user_id, sync_type, mode, status, requested_count, synced_count,
                   geocoded_count, geocoding_failed_count, skipped_count,
                   rate_limit_limit, rate_limit_usage, error_message, started_at,
                   progress_updated_at, finished_at
            FROM sync_logs
            WHERE user_id = #{userId}
              AND mode = #{mode}
              AND status = 'STARTED'
              AND progress_updated_at > NOW() - INTERVAL '2 minutes'
            ORDER BY started_at DESC
            LIMIT 1
            """)
    Optional<SyncLog> findRunningByUserIdAndMode(@Param("userId") Long userId, @Param("mode") String mode);

    @Update("""
            UPDATE sync_logs
            SET requested_count = #{requestedCount},
                synced_count = #{syncedCount},
                geocoded_count = #{geocodedCount},
                geocoding_failed_count = #{geocodingFailedCount},
                skipped_count = #{skippedCount},
                rate_limit_limit = #{rateLimitLimit},
                rate_limit_usage = #{rateLimitUsage},
                progress_updated_at = NOW()
            WHERE id = #{id}
            """)
    int updateProgress(
            @Param("id") Long id,
            @Param("requestedCount") int requestedCount,
            @Param("syncedCount") int syncedCount,
            @Param("geocodedCount") int geocodedCount,
            @Param("geocodingFailedCount") int geocodingFailedCount,
            @Param("skippedCount") int skippedCount,
            @Param("rateLimitLimit") String rateLimitLimit,
            @Param("rateLimitUsage") String rateLimitUsage
    );

    @Update("""
            UPDATE sync_logs
            SET status = #{status},
                requested_count = #{requestedCount},
                synced_count = #{syncedCount},
                geocoded_count = #{geocodedCount},
                geocoding_failed_count = #{geocodingFailedCount},
                skipped_count = #{skippedCount},
                rate_limit_limit = #{rateLimitLimit},
                rate_limit_usage = #{rateLimitUsage},
                error_message = #{errorMessage},
                progress_updated_at = NOW(),
                finished_at = NOW()
            WHERE id = #{id}
            """)
    int finish(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("requestedCount") int requestedCount,
            @Param("syncedCount") int syncedCount,
            @Param("geocodedCount") int geocodedCount,
            @Param("geocodingFailedCount") int geocodingFailedCount,
            @Param("skippedCount") int skippedCount,
            @Param("rateLimitLimit") String rateLimitLimit,
            @Param("rateLimitUsage") String rateLimitUsage,
            @Param("errorMessage") String errorMessage
    );
}
