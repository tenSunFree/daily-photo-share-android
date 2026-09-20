package com.sun.daily_photo_share_android.core.model

import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyShareMediaNamingTest {

    @Test
    fun relativePathFor_buildsIsoDateFolder() {
        assertEquals(
            "Pictures/DailyShare/2026-09-19/",
            DailyShareMediaNaming.relativePathFor(LocalDate.of(2026, 9, 19)),
        )
    }

    @Test
    fun fileNameFor_containsMilliseconds() {
        val capturedAt = LocalDateTime.of(2026, 9, 19, 10, 35, 27, 789_000_000)
        assertEquals("IMG_20260919_103527_789.jpg", DailyShareMediaNaming.fileNameFor(capturedAt))
    }

    @Test
    fun fileNameFor_burstShotsDoNotCollide() {
        val first = LocalDateTime.of(2026, 9, 19, 10, 35, 27, 100_000_000)
        val second = LocalDateTime.of(2026, 9, 19, 10, 35, 27, 350_000_000)
        assertNotEquals(
            DailyShareMediaNaming.fileNameFor(first),
            DailyShareMediaNaming.fileNameFor(second),
        )
    }

    @Test
    fun crossMidnight_producesDifferentFolders() {
        val before = LocalDateTime.of(2026, 9, 19, 23, 59, 50)
        val after = LocalDateTime.of(2026, 9, 20, 0, 1, 10)

        assertEquals(
            "Pictures/DailyShare/2026-09-19/",
            DailyShareMediaNaming.relativePathFor(before.toLocalDate()),
        )
        assertEquals(
            "Pictures/DailyShare/2026-09-20/",
            DailyShareMediaNaming.relativePathFor(after.toLocalDate()),
        )
    }

    @Test
    fun isDailySharePath_isStrict() {
        assertTrue(DailyShareMediaNaming.isDailySharePath("Pictures/DailyShare/2026-09-19/"))
        assertFalse(DailyShareMediaNaming.isDailySharePath("Pictures/DailyShareOther/"))
        assertFalse(DailyShareMediaNaming.isDailySharePath("Pictures/DailyShare"))
        assertFalse(DailyShareMediaNaming.isDailySharePath("DCIM/Camera/"))
        assertFalse(DailyShareMediaNaming.isDailySharePath(null))
    }

    @Test
    fun dateFromRelativePath_parsesValidFolder() {
        assertEquals(
            LocalDate.of(2026, 9, 19),
            DailyShareMediaNaming.dateFromRelativePath("Pictures/DailyShare/2026-09-19/"),
        )
    }

    @Test
    fun dateFromRelativePath_returnsNullForInvalidInput() {
        assertNull(DailyShareMediaNaming.dateFromRelativePath("Pictures/DailyShare/notes/"))
        assertNull(DailyShareMediaNaming.dateFromRelativePath("Pictures/DailyShare/2026-13-40/"))
        assertNull(DailyShareMediaNaming.dateFromRelativePath("DCIM/Camera/"))
        assertNull(DailyShareMediaNaming.dateFromRelativePath(null))
    }
}