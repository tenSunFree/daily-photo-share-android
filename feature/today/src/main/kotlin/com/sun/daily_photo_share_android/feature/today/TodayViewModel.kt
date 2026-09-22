package com.sun.daily_photo_share_android.feature.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.daily_photo_share_android.core.domain.GetTodaySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val getTodaySummary: GetTodaySummaryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TodayUiState())
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    private val _effects = Channel<TodayEffect>(Channel.BUFFERED)
    val effects: Flow<TodayEffect> = _effects.receiveAsFlow()

    private var loadJob: Job? = null

    // No init block: the screen sends Refresh on every resume, which covers the first display,
    // returning from another screen, and returning after the calendar date has changed.
    fun onIntent(intent: TodayIntent) {
        when (intent) {
            TodayIntent.Refresh -> load()
            TodayIntent.CaptureClicked -> _effects.trySend(TodayEffect.NavigateToCamera)
            TodayIntent.SelectPhotosForLineClicked ->
                _effects.trySend(TodayEffect.NavigateToGallerySelection)
        }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            reduce(TodayMutation.LoadStarted)
            getTodaySummary()
                .catch { reduce(TodayMutation.Failed(TodayError.LoadFailed)) }
                .collect { summary -> reduce(TodayMutation.Loaded(summary)) }
        }
    }

    private fun reduce(mutation: TodayMutation) {
        _state.update { current -> TodayReducer.reduce(current, mutation) }
    }
}