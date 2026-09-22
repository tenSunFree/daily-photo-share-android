package com.sun.daily_photo_share_android.feature.today

/** Pure function: (old state, mutation) -> new state. No Android, no coroutines. */
internal object TodayReducer {

    fun reduce(state: TodayUiState, mutation: TodayMutation): TodayUiState = when (mutation) {
        // Keep showing existing content while refreshing; show the spinner only when there is nothing to show.
        TodayMutation.LoadStarted -> state.copy(
            isLoading = state.summary == null,
            error = null,
        )

        is TodayMutation.Loaded -> TodayUiState(
            isLoading = false,
            summary = mutation.summary,
            error = null,
        )

        // A failed refresh keeps the last good summary on screen.
        is TodayMutation.Failed -> state.copy(
            isLoading = false,
            error = mutation.error,
        )
    }
}