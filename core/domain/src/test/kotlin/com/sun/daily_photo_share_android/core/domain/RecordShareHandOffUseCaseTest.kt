package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RecordShareHandOffUseCaseTest {

    private class RecordingRepository : ShareHistoryRepository {
        val recorded = mutableListOf<Pair<ShareRequest, Instant>>()

        override suspend fun recordHandOff(request: ShareRequest, at: Instant) {
            recorded += request to at
        }

        override fun observeLatest(): Flow<ShareRecord?> = emptyFlow()
    }

    @Test
    fun recordsTheRequestAtTheClockTime() = runTest {
        val now = Instant.parse("2026-10-08T10:00:00Z")
        val repository = RecordingRepository()
        val request = ShareRequest(
            listOf(MediaUri("content://media/external/images/media/1")),
            ShareTarget.LINE,
        )
        RecordShareHandOffUseCase(repository, Clock.fixed(now, ZoneOffset.UTC))(request)
        assertEquals(listOf(request to now), repository.recorded)
    }
}