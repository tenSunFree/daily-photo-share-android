package com.sun.daily_photo_share_android.core.media

import com.sun.daily_photo_share_android.core.model.MediaUri
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaStoreMapperTest {

    private val uri = MediaUri("content://media/external/images/media/123")

    private fun row(
        dateTakenMillis: Long? = 1_758_000_000_000,
        displayName: String? = "IMG_20260919_103527_789.jpg",
        mimeType: String? = "image/jpeg",
        relativePath: String? = "Pictures/DailyShare/2026-09-19/",
        bucketDisplayName: String? = "2026-09-19",
    ) = MediaStoreRow(
        id = 123,
        displayName = displayName,
        mimeType = mimeType,
        dateTakenMillis = dateTakenMillis,
        dateAddedSeconds = 1_758_000_500,
        width = 4000,
        height = 3000,
        relativePath = relativePath,
        bucketId = 10,
        bucketDisplayName = bucketDisplayName,
    )

    @Test
    fun mapsAllColumns() {
        val photo = MediaStoreMapper.toMediaPhoto(row(), uri)

        assertEquals(123L, photo.id)
        assertEquals(uri, photo.uri)
        assertEquals("IMG_20260919_103527_789.jpg", photo.displayName)
        assertEquals("image/jpeg", photo.mimeType)
        assertEquals(Instant.ofEpochMilli(1_758_000_000_000), photo.takenAt)
        assertEquals(Instant.ofEpochSecond(1_758_000_500), photo.dateAdded)
        assertEquals(4000, photo.width)
        assertEquals(3000, photo.height)
        assertEquals(10L, photo.bucketId)
        assertEquals("2026-09-19", photo.bucketName)
    }

    @Test
    fun missingTextColumns_getSafeDefaults() {
        val photo = MediaStoreMapper.toMediaPhoto(
            row(displayName = null, mimeType = null, bucketDisplayName = null),
            uri,
        )

        assertEquals("", photo.displayName)
        assertEquals("image/*", photo.mimeType)
        assertEquals("", photo.bucketName)
    }

    @Test
    fun dailySharePath_isRecognised() {
        assertTrue(MediaStoreMapper.toMediaPhoto(row(), uri).isDailySharePhoto)
        assertFalse(
            MediaStoreMapper.toMediaPhoto(
                row(relativePath = "DCIM/Camera/"),
                uri
            ).isDailySharePhoto
        )
    }

    @Test
    fun takenAt_usesDateTaken_whenPresent() {
        assertEquals(
            Instant.ofEpochMilli(1_758_000_000_000),
            resolveTakenAt(1_758_000_000_000, 1_758_000_500)
        )
    }

    @Test
    fun takenAt_fallsBackToDateAdded_whenDateTakenIsNull() {
        assertEquals(Instant.ofEpochSecond(1_758_000_500), resolveTakenAt(null, 1_758_000_500))
    }

    @Test
    fun takenAt_fallsBackToDateAdded_whenDateTakenIsZero() {
        assertEquals(Instant.ofEpochSecond(1_758_000_500), resolveTakenAt(0L, 1_758_000_500))
    }
}