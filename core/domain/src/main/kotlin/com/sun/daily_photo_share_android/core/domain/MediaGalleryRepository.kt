package com.sun.daily_photo_share_android.core.domain

import androidx.paging.PagingData
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import kotlinx.coroutines.flow.Flow

interface MediaGalleryRepository {
    /** Images the app can read through MediaStore, newest first; re-emits when MediaStore changes. */
    fun observePhotos(): Flow<PagingData<MediaPhoto>>
}