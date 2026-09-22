package com.sun.daily_photo_share_android.feature.today

import com.sun.daily_photo_share_android.core.domain.DateProvider
import com.sun.daily_photo_share_android.core.domain.GetTodaySummaryUseCase
import com.sun.daily_photo_share_android.core.domain.TodaySummaryRepository
import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val date = LocalDate.of(2026, 9, 19)
    private val summary = TodaySummary(date, 23, emptyList(), null, null)
    private val newerSummary = summary.copy(photoCount = 24)

    private class FakeRepository(
        private val source: (LocalDate) -> Flow<TodaySummary>,
    ) : TodaySummaryRepository {
        override fun observeSummary(date: LocalDate): Flow<TodaySummary> = source(date)
    }

    private fun viewModel(
        dateProvider: DateProvider = DateProvider { date },
        source: (LocalDate) -> Flow<TodaySummary>,
    ) = TodayViewModel(GetTodaySummaryUseCase(FakeRepository(source), dateProvider))

    @Test
    fun initialState_isLoading_andNothingIsLoadedBeforeAnIntent() {
        var calls = 0
        val vm = viewModel { calls++; flowOf(summary) }

        assertTrue(vm.state.value.isLoading)
        assertNull(vm.state.value.summary)
        assertEquals(0, calls)
    }

    @Test
    fun refresh_loadsSummary() {
        val vm = viewModel { flowOf(summary) }

        vm.onIntent(TodayIntent.Refresh)

        assertFalse(vm.state.value.isLoading)
        assertEquals(summary, vm.state.value.summary)
        assertNull(vm.state.value.error)
    }

    @Test
    fun refresh_failure_reportsError() {
        val vm = viewModel { flow { throw IOException("boom") } }

        vm.onIntent(TodayIntent.Refresh)

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.summary)
        assertEquals(TodayError.LoadFailed, vm.state.value.error)
    }

    @Test
    fun refresh_failureAfterSuccess_keepsLastSummary() {
        var call = 0
        val vm = viewModel {
            if (call++ == 0) flowOf(summary) else flow { throw IOException("boom") }
        }

        vm.onIntent(TodayIntent.Refresh)
        vm.onIntent(TodayIntent.Refresh)

        assertEquals(summary, vm.state.value.summary)
        assertEquals(TodayError.LoadFailed, vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun refresh_successAfterFailure_clearsError() {
        var call = 0
        val vm = viewModel {
            if (call++ == 0) flow { throw IOException("boom") } else flowOf(summary)
        }

        vm.onIntent(TodayIntent.Refresh)
        vm.onIntent(TodayIntent.Refresh)

        assertEquals(summary, vm.state.value.summary)
        assertNull(vm.state.value.error)
    }

    @Test
    fun refresh_cancelsThePreviousObservation() = runTest {
        val first = MutableSharedFlow<TodaySummary>()
        var call = 0
        val vm = viewModel { if (call++ == 0) first else flowOf(newerSummary) }

        vm.onIntent(TodayIntent.Refresh)
        vm.onIntent(TodayIntent.Refresh)
        first.emit(summary)

        assertEquals(newerSummary, vm.state.value.summary)
    }

    @Test
    fun refresh_readsTheCurrentDateEachTime_soMidnightIsCrossed() {
        var today = LocalDate.of(2026, 9, 19)
        val requested = mutableListOf<LocalDate>()
        val vm = viewModel(dateProvider = { today }) {
            requested += it
            flowOf(summary.copy(date = it))
        }

        vm.onIntent(TodayIntent.Refresh)
        today = LocalDate.of(2026, 9, 20)
        vm.onIntent(TodayIntent.Refresh)

        assertEquals(
            listOf(LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 20)),
            requested,
        )
        assertEquals(LocalDate.of(2026, 9, 20), vm.state.value.summary?.date)
    }

    @Test
    fun captureClicked_emitsNavigateToCamera() = runTest {
        val vm = viewModel { flowOf(summary) }

        vm.onIntent(TodayIntent.CaptureClicked)

        assertEquals(TodayEffect.NavigateToCamera, vm.effects.first())
    }

    @Test
    fun selectPhotosForLineClicked_emitsNavigateToGallerySelection() = runTest {
        val vm = viewModel { flowOf(summary) }

        vm.onIntent(TodayIntent.SelectPhotosForLineClicked)

        assertEquals(TodayEffect.NavigateToGallerySelection, vm.effects.first())
    }
}