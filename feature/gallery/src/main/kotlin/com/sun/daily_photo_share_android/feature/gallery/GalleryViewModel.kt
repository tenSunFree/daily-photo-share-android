package com.sun.daily_photo_share_android.feature.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sun.daily_photo_share_android.core.domain.GetPhotoPermissionStateUseCase
import com.sun.daily_photo_share_android.core.domain.GetRequestablePermissionsUseCase
import com.sun.daily_photo_share_android.core.domain.ObserveDevicePhotosUseCase
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getPermissionState: GetPhotoPermissionStateUseCase,
    private val getRequestablePermissions: GetRequestablePermissionsUseCase,
    private val observeDevicePhotos: ObserveDevicePhotosUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    internal companion object {
        const val SELECTION_KEY = "gallery_selection"
    }

    private val _state = MutableStateFlow(GalleryUiState())
    val state: StateFlow<GalleryUiState> = _state.asStateFlow()
    private val _effects = Channel<GalleryEffect>(Channel.BUFFERED)
    val effects: Flow<GalleryEffect> = _effects.receiveAsFlow()

    /** Restarts the MediaStore query only when the permission or the readable set may have changed. */
    val photos: Flow<PagingData<MediaPhoto>> = _state
        .map { it.permissionState to it.accessRevision }
        .distinctUntilChanged()
        .flatMapLatest { (permissionState, _) ->
            when (permissionState) {
                PhotoPermissionState.Full, PhotoPermissionState.Partial -> observeDevicePhotos()
                PhotoPermissionState.Denied, null -> flowOf(PagingData.empty<MediaPhoto>())
            }
        }
        .cachedIn(viewModelScope)

    init {
        // Restores saved state after process death; this is not a data load.
        savedStateHandle.get<List<String>>(SELECTION_KEY)?.let { saved ->
            reduce(GalleryMutation.SelectionRestored(saved.map(::MediaUri)))
        }
    }

    fun onIntent(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.CheckPermission ->
                reduce(GalleryMutation.PermissionChecked(getPermissionState()))
            // Re-requesting shows the system photo selection again for partial access.
            GalleryIntent.RequestPermissionClicked,
            GalleryIntent.ManageSelectedPhotosClicked,
                -> _effects.trySend(GalleryEffect.RequestPermissions(getRequestablePermissions()))

            GalleryIntent.OpenSettingsClicked -> _effects.trySend(GalleryEffect.OpenAppSettings)
            is GalleryIntent.PhotoTapped -> reduce(GalleryMutation.PhotoTapped(intent.uri))
            is GalleryIntent.PhotoLongPressed -> reduce(GalleryMutation.PhotoLongPressed(intent.uri))
            GalleryIntent.ClearSelectionClicked -> reduce(GalleryMutation.SelectionCleared)
            GalleryIntent.SelectionModeRequested -> reduce(GalleryMutation.SelectionModeRequested)
        }
    }

    private fun reduce(mutation: GalleryMutation) {
        val previousSelection = _state.value.selection
        _state.update { current -> GalleryReducer.reduce(current, mutation) }
        val selection = _state.value.selection
        if (selection != previousSelection) {
            savedStateHandle[SELECTION_KEY] = ArrayList(selection.items.map { it.value })
        }
    }
}