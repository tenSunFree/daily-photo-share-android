package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.TodaySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GetTodaySummaryUseCase(
    private val repository: TodaySummaryRepository,
    private val dateProvider: DateProvider,
) {
    /** The date is read when the flow is collected, not when it is created, so each collection sees the current date. */
    operator fun invoke(): Flow<TodaySummary> = flow {
        emitAll(repository.observeSummary(dateProvider.today()))
    }
}