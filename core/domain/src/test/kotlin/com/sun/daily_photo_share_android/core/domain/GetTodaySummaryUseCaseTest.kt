package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget
import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    private class FakeShareHistoryRepository(
        private val latest: Flow<ShareRecord?> = flowOf(null),
    ) : ShareHistoryRepository {
        override suspend fun recordHandOff(request: ShareRequest, at: Instant) = Unit

        override fun observeLatest(): Flow<ShareRecord?> = latest
    }

    private val date = LocalDate.of(2026, 10, 10)

    private fun summaryWith(photoCount: Int) = TodaySummary(
        date = date,
        photoCount = photoCount,
        recentPhotos = emptyList(),
        lastCapturedAt = null,
        lastShare = null,
    )

    private fun todayRepository(summary: TodaySummary) = object : TodaySummaryRepository {
        override fun observeSummary(date: LocalDate): Flow<TodaySummary> = flowOf(summary)
    }

    // --- date handling ---

    @Test
    fun usesDateFromProvider() = runTest {
        val repository = RecordingRepository()
        val provider = MutableDateProvider(LocalDate.of(2026, 9, 19))

        val summary =
            GetTodaySummaryUseCase(repository, FakeShareHistoryRepository(), provider)().first()

        assertEquals(LocalDate.of(2026, 9, 19), summary.date)
    }

    @Test
    fun eachCollection_readsDateAgain_soMidnightIsCrossed() = runTest {
        val repository = RecordingRepository()
        val provider = MutableDateProvider(LocalDate.of(2026, 9, 19))
        val useCase = GetTodaySummaryUseCase(repository, FakeShareHistoryRepository(), provider)

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

        GetTodaySummaryUseCase(
            repository,
            FakeShareHistoryRepository(),
            MutableDateProvider(LocalDate.of(2026, 9, 19)),
        )()

        assertEquals(emptyList<LocalDate>(), repository.requestedDates)
    }

    // --- share history ---

    @Test
    fun combinesTheLatestShareIntoTheSummary_keepingTheRestOfIt() = runTest {
        val share = ShareRecord(
            id = 7,
            handedOffAt = Instant.parse("2026-10-10T08:30:00Z"),
            photoCount = 3,
            target = ShareTarget.LINE,
        )

        val result = GetTodaySummaryUseCase(
            todayRepository(summaryWith(photoCount = 5)),
            FakeShareHistoryRepository(flowOf(share)),
            MutableDateProvider(date),
        )().first()

        assertEquals(share, result.lastShare)
        assertEquals(5, result.photoCount)
    }

    @Test
    fun lastShareIsNull_whenNothingWasShared() = runTest {
        val result = GetTodaySummaryUseCase(
            todayRepository(summaryWith(photoCount = 5)),
            FakeShareHistoryRepository(flowOf(null)),
            MutableDateProvider(date),
        )().first()

        assertNull(result.lastShare)
    }

    @Test
    fun newestShareWins_whenTheHistoryEmitsAgain() = runTest {
        val first = ShareRecord(1, Instant.parse("2026-10-10T08:00:00Z"), 1, ShareTarget.LINE)
        val second =
            ShareRecord(2, Instant.parse("2026-10-10T09:00:00Z"), 4, ShareTarget.SYSTEM_SHEET)

        val results = GetTodaySummaryUseCase(
            todayRepository(summaryWith(photoCount = 0)),
            FakeShareHistoryRepository(flowOf(first, second)),
            MutableDateProvider(date),
        )().toList()

        assertEquals(second, results.last().lastShare)
    }
}