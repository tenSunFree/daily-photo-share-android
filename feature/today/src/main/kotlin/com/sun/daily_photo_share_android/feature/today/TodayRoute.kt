package com.sun.daily_photo_share_android.feature.today

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

/** Connects [TodayViewModel] to [TodayScreen]: collects state, forwards intents, turns effects into navigation. */
@Composable
fun TodayRoute(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallerySelection: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Every resume reloads, so the screen also picks up a changed calendar date.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(TodayIntent.Refresh)
    }

    val currentOnNavigateToCamera by rememberUpdatedState(onNavigateToCamera)
    val currentOnNavigateToGallerySelection by rememberUpdatedState(onNavigateToGallerySelection)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Effects are collected only while the screen is started; the channel keeps them until then.
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    TodayEffect.NavigateToCamera -> currentOnNavigateToCamera()
                    TodayEffect.NavigateToGallerySelection -> currentOnNavigateToGallerySelection()
                }
            }
        }
    }

    TodayScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}