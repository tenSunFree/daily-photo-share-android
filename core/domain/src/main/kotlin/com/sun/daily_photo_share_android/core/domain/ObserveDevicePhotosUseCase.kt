package com.sun.daily_photo_share_android.core.domain

import androidx.paging.PagingData
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import kotlinx.coroutines.flow.Flow

class ObserveDevicePhotosUseCase(
    private val repository: MediaGalleryRepository,
) {
    operator fun invoke(): Flow<PagingData<MediaPhoto>> = repository.observePhotos()
}