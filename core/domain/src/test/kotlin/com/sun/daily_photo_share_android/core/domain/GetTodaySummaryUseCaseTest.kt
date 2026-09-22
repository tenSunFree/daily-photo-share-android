package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTodaySummaryUseCaseTest {

    private class RecordingRepository : TodaySummaryRepository {
        val requestedDates = mutableListOf<LocalDate>()

        override fun observeSummary(date: LocalDate): Flow<TodaySummary> {
            requestedDates += date
            return flowOf(
                TodaySummary(
                    date = date,
                    photoCount = 0,
                    recentPhotos = emptyList(),
                    lastCapturedAt = null,
                    lastShare = null,
                ),
            )
        }
    }

    private class MutableDateProvider(var date: LocalDate) : DateProvider {
        override fun today(): LocalDate = date
    }

    @Test
    fun usesDateFromProvider() = runTest {
        val repository = RecordingRepository()
        val provider = MutableDateProvider(LocalDate.of(2026, 9, 19))

        val summary = GetTodaySummaryUseCase(repository, provider)().first()

        assertEquals(LocalDate.of(2026, 9, 19), summary.date)
    }

    @Test
    fun eachCollection_readsDateAgain_soMidnightIsCrossed() = runTest {
        val repository = RecordingRepository()
        val provider = MutableDateProvider(LocalDate.of(2026, 9, 19))
        val useCase = GetTodaySummaryUseCase(repository, provider)

        assertEquals(LocalDate.of(2026, 9, 19), useCase().first().date)
        provider.date = LocalDate.of(2026, 9, 20)
        assertEquals(LocalDate.of(2026, 9, 20), useCase().first().date)

        assertEquals(
            listOf(LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 20)),
            repository.requestedDates,
        )
    }

    @Test
    fun invoke_doesNotTouchRepositoryUntilCollected() {
        val repository = RecordingRepository()

        GetTodaySummaryUseCase(repository, MutableDateProvider(LocalDate.of(2026, 9, 19)))()

        assertEquals(emptyList<LocalDate>(), repository.requestedDates)
    }
}