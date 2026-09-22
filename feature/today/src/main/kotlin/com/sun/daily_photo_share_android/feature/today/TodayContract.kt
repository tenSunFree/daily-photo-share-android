package com.sun.daily_photo_share_android.feature.today

import com.sun.daily_photo_share_android.core.model.TodaySummary

/** The single immutable state of the Today screen. */
data class TodayUiState(
    val isLoading: Boolean = true,
    val summary: TodaySummary? = null,
    val error: TodayError? = null,
)

enum class TodayError { LoadFailed }

/** What the user (or the screen lifecycle) asks for. */
sealed interface TodayIntent {
    data object Refresh : TodayIntent
    data object CaptureClicked : TodayIntent
    data object SelectPhotosForLineClicked : TodayIntent
}

/** One-off events. Delivered once, never kept in [TodayUiState]. */
sealed interface TodayEffect {
    data object NavigateToCamera : TodayEffect
    data object NavigateToGallerySelection : TodayEffect
}

/** Results produced by the ViewModel that the reducer folds into the state. */
internal sealed interface TodayMutation {
    data object LoadStarted : TodayMutation
    data class Loaded(val summary: TodaySummary) : TodayMutation
    data class Failed(val error: TodayError) : TodayMutation
}