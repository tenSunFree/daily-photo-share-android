package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface ShareHistoryRepository {
    suspend fun recordHandOff(request: ShareRequest, at: Instant)

    /** The most recent hand-off, or null when nothing has been shared yet. */
    fun observeLatest(): Flow<ShareRecord?>
}