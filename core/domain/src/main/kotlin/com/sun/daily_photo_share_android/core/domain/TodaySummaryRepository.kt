package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface TodaySummaryRepository {
    /** Emits the summary for [date] and emits again whenever the underlying data changes. */
    fun observeSummary(date: LocalDate): Flow<TodaySummary>
}