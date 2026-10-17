package com.sun.daily_photo_share_android.core.database

import com.sun.daily_photo_share_android.core.domain.ShareHistoryRepository
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

internal class RoomShareHistoryRepository @Inject constructor(
    private val dao: ShareRecordDao,
) : ShareHistoryRepository {

    override suspend fun recordHandOff(request: ShareRequest, at: Instant) {
        dao.insert(request.toEntity(at))
    }

    override fun observeLatest(): Flow<ShareRecord?> =
        dao.observeLatest().map { it?.toModel() }
}