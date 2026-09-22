package com.sun.daily_photo_share_android.feature.today

import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodayReducerTest {

    private val summary = TodaySummary(
        date = LocalDate.of(2026, 9, 19),
        photoCount = 23,
        recentPhotos = emptyList(),
        lastCapturedAt = null,
        lastShare = null,
    )

    @Test
    fun initialState_isLoadingWithoutData() {
        val state = TodayUiState()

        assertTrue(state.isLoading)
        assertNull(state.summary)
        assertNull(state.error)
    }

    @Test
    fun loadStarted_withoutSummary_showsLoading() {
        val result = TodayReducer.reduce(TodayUiState(), TodayMutation.LoadStarted)

        assertTrue(result.isLoading)
    }

    @Test
    fun loadStarted_withSummary_keepsContentVisibleAndClearsError() {
        val state = TodayUiState(
            isLoading = false,
            summary = summary,
            error = TodayError.LoadFailed,
        )

        val result = TodayReducer.reduce(state, TodayMutation.LoadStarted)

        assertFalse(result.isLoading)
        assertEquals(summary, result.summary)
        assertNull(result.error)
    }

    @Test
    fun loaded_replacesSummaryAndClearsError() {
        val state = TodayUiState(isLoading = true, error = TodayError.LoadFailed)

        val result = TodayReducer.reduce(state, TodayMutation.Loaded(summary))

        assertFalse(result.isLoading)
        assertEquals(summary, result.summary)
        assertNull(result.error)
    }

    @Test
    fun failed_withoutSummary_reportsError() {
        val result = TodayReducer.reduce(
            TodayUiState(),
            TodayMutation.Failed(TodayError.LoadFailed),
        )

        assertFalse(result.isLoading)
        assertNull(result.summary)
        assertEquals(TodayError.LoadFailed, result.error)
    }

    @Test
    fun failed_withSummary_keepsSummary() {
        val state = TodayUiState(isLoading = false, summary = summary)

        val result = TodayReducer.reduce(state, TodayMutation.Failed(TodayError.LoadFailed))

        assertEquals(summary, result.summary)
        assertEquals(TodayError.LoadFailed, result.error)
    }
}