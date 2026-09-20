package com.sun.daily_photo_share_android.core.model

import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaPhotoTest {

    private fun photo(relativePath: String?) = MediaPhoto(
        id = 1,
        uri = MediaUri("content://media/external/images/media/1"),
        displayName = "IMG_20260919_103527_789.jpg",
        mimeType = "image/jpeg",
        takenAt = Instant.EPOCH,
        dateAdded = Instant.EPOCH,
        width = 4000,
        height = 3000,
        relativePath = relativePath,
        bucketId = 42,
        bucketName = "2026-09-19",
    )

    @Test
    fun isDailySharePhoto_trueOnlyInsideDailyShareTree() {
        assertTrue(photo("Pictures/DailyShare/2026-09-19/").isDailySharePhoto)
        assertFalse(photo("DCIM/Camera/").isDailySharePhoto)
        assertFalse(photo("Pictures/Screenshots/").isDailySharePhoto)
        assertFalse(photo(null).isDailySharePhoto)
    }
}