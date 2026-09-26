package com.sun.daily_photo_share_android.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sun.daily_photo_share_android.core.domain.GetPhotoPermissionStateUseCase
import com.sun.daily_photo_share_android.core.domain.GetRequestablePermissionsUseCase
import com.sun.daily_photo_share_android.core.domain.ObserveDevicePhotosUseCase
import com.sun.daily_photo_share_android.core.model.MediaPhoto
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
) : ViewModel() {

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

    fun onIntent(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.CheckPermission -> checkPermission()
            // Re-requesting shows the system photo selection again for partial access.
            GalleryIntent.RequestPermissionClicked,
            GalleryIntent.ManageSelectedPhotosClicked,
                -> _effects.trySend(GalleryEffect.RequestPermissions(getRequestablePermissions()))
            GalleryIntent.OpenSettingsClicked -> _effects.trySend(GalleryEffect.OpenAppSettings)
        }
    }

    private fun checkPermission() {
        reduce(GalleryMutation.PermissionChecked(getPermissionState()))
    }

    private fun reduce(mutation: GalleryMutation) {
        _state.update { current -> GalleryReducer.reduce(current, mutation) }
    }
}