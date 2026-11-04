package com.sun.daily_photo_share_android.core.domain

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class PrepareCaptureUseCaseTest {

    @Test
    fun invoke_buildsDailyShareTargetInTheClockZone() {
        val instant = Instant.parse("2026-10-20T16:30:12.123Z")
        val clock = Clock.fixed(instant, ZoneId.of("Asia/Taipei"))

        val result = PrepareCaptureUseCase(clock)()

        assertEquals("IMG_20261021_003012_123.jpg", result.displayName)
        assertEquals("Pictures/DailyShare/2026-10-21/", result.relativePath)
        assertEquals("image/jpeg", result.mimeType)
        assertEquals(instant, result.capturedAt)
    }
}