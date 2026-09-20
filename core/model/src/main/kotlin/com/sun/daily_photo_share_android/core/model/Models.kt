package com.sun.daily_photo_share_android.core.model

import java.time.Instant
import java.time.LocalDate

data class MediaPhoto(
    val id: Long,
    val uri: MediaUri,
    val displayName: String,
    val mimeType: String,
    val takenAt: Instant,
    val dateAdded: Instant,
    val width: Int,
    val height: Int,
    val relativePath: String?,
    val bucketId: Long,
    val bucketName: String,
) {
    val isDailySharePhoto: Boolean
        get() = DailyShareMediaNaming.isDailySharePath(relativePath)
}

data class MediaAlbum(
    val bucketId: Long,
    val name: String,
    val coverUri: MediaUri?,
    val photoCount: Int,
    val lastModifiedAt: Instant?,
)

data class DailyAlbum(
    val date: LocalDate,
    val photoCount: Int,
    val coverUri: MediaUri?,
    val lastCapturedAt: Instant?,
)

enum class ShareTarget { LINE, SYSTEM_SHEET }

/** HANDED_OFF:App only proves that the task was "handed off to another app"; it cannot prove that the other party actually sent or received it. */
enum class ShareStatus { HANDED_OFF }

data class ShareRecord(
    val id: Long,
    val handedOffAt: Instant,
    val photoCount: Int,
    val target: ShareTarget,
    val status: ShareStatus = ShareStatus.HANDED_OFF,
)

data class TodaySummary(
    val date: LocalDate,
    /** Only counts photos taken by DailyShare today, not all new photos on the device. */
    val photoCount: Int,
    val recentPhotos: List<MediaPhoto>,
    val lastCapturedAt: Instant?,
    val lastShare: ShareRecord?,
)

enum class PhotoPermissionState { Full, Partial, Denied }