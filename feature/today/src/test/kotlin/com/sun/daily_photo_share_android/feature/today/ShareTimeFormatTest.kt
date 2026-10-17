package com.sun.daily_photo_share_android.feature.today

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareTimeFormatTest {

    private val today = LocalDate.of(2026, 10, 10)

    @Test
    fun aShareFromToday_showsTheTimeOnly() {
        val at = Instant.parse("2026-10-10T08:32:00Z")
        assertEquals("08:32", formatShareTime(at, today, ZoneOffset.UTC))
    }

    @Test
    fun aShareFromAnEarlierDay_showsTheDateToo() {
        val at = Instant.parse("2026-10-08T16:32:00Z")
        assertEquals("10/8 16:32", formatShareTime(at, today, ZoneOffset.UTC))
    }

    @Test
    fun theDayIsJudgedInTheGivenZone_notInUtc() {
        // 23:30 UTC on the 9th is already the 10th in UTC+8.
        val at = Instant.parse("2026-10-09T23:30:00Z")
        assertEquals("07:30", formatShareTime(at, today, ZoneOffset.ofHours(8)))
    }
}