package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.ShareRequest
import java.time.Clock

class RecordShareHandOffUseCase(
    private val repository: ShareHistoryRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(request: ShareRequest) {
        repository.recordHandOff(request, clock.instant())
    }
}