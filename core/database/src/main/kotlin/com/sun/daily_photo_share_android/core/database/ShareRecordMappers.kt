package com.sun.daily_photo_share_android.core.database

import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareStatus
import com.sun.daily_photo_share_android.core.model.ShareTarget
import java.time.Instant

internal fun ShareRequest.toEntity(at: Instant): ShareRecordEntity = ShareRecordEntity(
    handedOffAtEpochMillis = at.toEpochMilli(),
    photoCount = uris.size,
    target = target.name,
    status = ShareStatus.HANDED_OFF.name,
)

internal fun ShareRecordEntity.toModel(): ShareRecord = ShareRecord(
    id = id,
    handedOffAt = Instant.ofEpochMilli(handedOffAtEpochMillis),
    photoCount = photoCount,
    target = ShareTarget.valueOf(target),
    status = ShareStatus.valueOf(status),
)