package com.sun.daily_photo_share_android.feature.camera

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sun.daily_photo_share_android.core.domain.PrepareCaptureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val prepareCapture: PrepareCaptureUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(CameraUiState())
    val state: StateFlow<CameraUiState> = _state.asStateFlow()

    private val _effects = Channel<CameraEffect>(Channel.BUFFERED)
    val effects: Flow<CameraEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: CameraIntent) {
        when (intent) {
            is CameraIntent.PermissionObserved -> reduce(
                CameraMutation.PermissionChecked(
                    CameraPermissionResolver.resolve(
                        granted = intent.granted,
                        shouldShowRationale = intent.shouldShowRationale,
                        hasRequestedBefore = savedStateHandle[PERMISSION_REQUESTED_KEY] ?: false,
                    ),
                ),
            )

            CameraIntent.RequestPermissionClicked -> {
                savedStateHandle[PERMISSION_REQUESTED_KEY] = true
                _effects.trySend(CameraEffect.RequestPermission)
            }

            CameraIntent.OpenSettingsClicked -> _effects.trySend(CameraEffect.OpenSettings)

            CameraIntent.CameraStarting -> reduce(CameraMutation.CameraStarting)
            CameraIntent.CameraReady -> reduce(CameraMutation.CameraReady)
            CameraIntent.CameraUnavailable -> reduce(CameraMutation.CameraUnavailable)

            CameraIntent.CaptureClicked -> {
                if (!_state.value.canCapture) return
                reduce(CameraMutation.CaptureStarted)
                _effects.trySend(CameraEffect.CapturePhoto(prepareCapture()))
            }

            CameraIntent.CaptureSucceeded -> reduce(CameraMutation.CaptureSucceeded)

            CameraIntent.CaptureFailed -> {
                reduce(CameraMutation.CaptureFailed)
                _effects.trySend(CameraEffect.ShowCaptureFailed)
            }
        }
    }

    private fun reduce(mutation: CameraMutation) {
        _state.update { CameraReducer.reduce(it, mutation) }
    }

    private companion object {
        const val PERMISSION_REQUESTED_KEY = "camera_permission_requested"
    }
}