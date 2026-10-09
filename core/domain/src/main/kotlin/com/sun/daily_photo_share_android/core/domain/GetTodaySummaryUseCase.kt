package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.TodaySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GetTodaySummaryUseCase(
    private val repository: TodaySummaryRepository,
    private val shareHistoryRepository: ShareHistoryRepository,
    private val dateProvider: DateProvider,
) {
    /** The date is read when the flow is collected, not when the use case is invoked. */
    operator fun invoke(): Flow<TodaySummary> = flow {
        emitAll(
            combine(
                repository.observeSummary(dateProvider.today()),
                shareHistoryRepository.observeLatest(),
            ) { summary, lastShare ->
                summary.copy(lastShare = lastShare)
            },
        )
    }
}