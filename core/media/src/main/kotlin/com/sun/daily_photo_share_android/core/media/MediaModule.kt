package com.sun.daily_photo_share_android.core.media

import com.sun.daily_photo_share_android.core.domain.MediaGalleryRepository
import com.sun.daily_photo_share_android.core.domain.PhotoPermissionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * :core:media binds its own implementations to the :core:domain interfaces.
 * Implementations are internal, so no other module can depend on them directly.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    @Binds
    abstract fun bindMediaGalleryRepository(impl: MediaGalleryRepositoryImpl): MediaGalleryRepository

    @Binds
    abstract fun bindPhotoPermissionRepository(impl: PhotoPermissionRepositoryImpl): PhotoPermissionRepository
}