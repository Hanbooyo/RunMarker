package com.stravamate.passport.repository;

import com.stravamate.passport.domain.entity.SyncLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SyncLogRepository {

    @Select("""
            INSERT INTO sync_logs (
                user_id, sync_type, status, requested_count, synced_count, skipped_count,
                started_at
            )
            VALUES (
                #{userId}, 'ACTIVITIES', 'STARTED', 0, 0, 0,
                NOW()
            )
            RETURNING id, user_id, sync_type, status, requested_count, synced_count,
                      skipped_count, error_message, started_at, finished_at
            """)
    SyncLog insertStarted(Long userId);

    @Update("""
            UPDATE sync_logs
            SET status = #{status},
                requested_count = #{requestedCount},
                synced_count = #{syncedCount},
                skipped_count = #{skippedCount},
                error_message = #{errorMessage},
                finished_at = NOW()
            WHERE id = #{id}
            """)
    int finish(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("requestedCount") int requestedCount,
            @Param("syncedCount") int syncedCount,
            @Param("skippedCount") int skippedCount,
            @Param("errorMessage") String errorMessage
    );
}
