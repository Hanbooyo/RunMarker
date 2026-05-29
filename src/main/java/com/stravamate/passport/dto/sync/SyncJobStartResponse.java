package com.stravamate.passport.dto.sync;

public record SyncJobStartResponse(
        Long syncLogId,
        String mode,
        String status
) {
}
