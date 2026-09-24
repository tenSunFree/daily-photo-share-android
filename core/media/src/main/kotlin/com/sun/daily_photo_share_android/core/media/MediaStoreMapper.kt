package com.sun.daily_photo_share_android.core.media

import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import java.time.Instant

/** Plain column values of one MediaStore row; reading them from a Cursor happens elsewhere. */
internal data class MediaStoreRow(
    val id: Long,
    val displayName: String?,
    val mimeType: String?,
    val dateTakenMillis: Long?,
    val dateAddedSeconds: Long,
    val width: Int,
    val height: Int,
    val relativePath: String?,
    val bucketId: Long,
    val bucketDisplayName: String?,
)

/** DATE_TAKEN is milliseconds and can be null or 0 (some screenshots); DATE_ADDED is seconds. */
internal fun resolveTakenAt(dateTakenMillis: Long?, dateAddedSeconds: Long): Instant =
    if (dateTakenMillis != null && dateTakenMillis > 0) {
        Instant.ofEpochMilli(dateTakenMillis)
    } else {
        Instant.ofEpochSecond(dateAddedSeconds)
    }

/** Pure mapping: the content URI is built by the caller, so no Android API is touched here. */
internal object MediaStoreMapper {
    fun toMediaPhoto(row: MediaStoreRow, uri: MediaUri): MediaPhoto = MediaPhoto(
        id = row.id,
        uri = uri,
        displayName = row.displayName.orEmpty(),
        mimeType = row.mimeType ?: "image/*",
        takenAt = resolveTakenAt(row.dateTakenMillis, row.dateAddedSeconds),
        dateAdded = Instant.ofEpochSecond(row.dateAddedSeconds),
        width = row.width,
        height = row.height,
        relativePath = row.relativePath,
        bucketId = row.bucketId,
        bucketName = row.bucketDisplayName.orEmpty(),
    )
}