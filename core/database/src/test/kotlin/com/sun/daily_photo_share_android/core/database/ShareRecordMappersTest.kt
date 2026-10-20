package com.sun.daily_photo_share_android.core.database

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareStatus
import com.sun.daily_photo_share_android.core.model.ShareTarget
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ShareRecordMappersTest {

    private val at = Instant.parse("2026-10-10T08:30:00.123Z")

    private val request = ShareRequest(
        listOf(
            MediaUri("content://media/external/images/media/1"),
            MediaUri("content://media/external/images/media/2"),
            MediaUri("content://media/external/images/media/3"),
        ),
        ShareTarget.SYSTEM_SHEET,
    )

    @Test
    fun request_becomesAHandedOffEntity() {
        assertEquals(
            ShareRecordEntity(
                handedOffAtEpochMillis = at.toEpochMilli(),
                photoCount = 3,
                target = "SYSTEM_SHEET",
                status = "HANDED_OFF",
            ),
            request.toEntity(at),
        )
    }

    @Test
    fun entity_roundTripsToTheModel_keepingMillisecondPrecision() {
        val entity = request.toEntity(at).copy(id = 9)

        assertEquals(
            ShareRecord(9, at, 3, ShareTarget.SYSTEM_SHEET, ShareStatus.HANDED_OFF),
            entity.toModel(),
        )
    }
}