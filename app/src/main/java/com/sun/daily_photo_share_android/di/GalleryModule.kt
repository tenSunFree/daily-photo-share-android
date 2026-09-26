package com.sun.daily_photo_share_android.di

import com.sun.daily_photo_share_android.core.domain.GetPhotoPermissionStateUseCase
import com.sun.daily_photo_share_android.core.domain.GetRequestablePermissionsUseCase
import com.sun.daily_photo_share_android.core.domain.MediaGalleryRepository
import com.sun.daily_photo_share_android.core.domain.ObserveDevicePhotosUseCase
import com.sun.daily_photo_share_android.core.domain.PhotoPermissionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Constructs the framework-free :core:domain use cases for the Gallery feature.
 * The repositories they need are bound by :core:media.
 */
@Module
@InstallIn(SingletonComponent::class)
object GalleryModule {

    @Provides
    fun provideGetPhotoPermissionStateUseCase(
        repository: PhotoPermissionRepository,
    ): GetPhotoPermissionStateUseCase = GetPhotoPermissionStateUseCase(repository)

    @Provides
    fun provideGetRequestablePermissionsUseCase(
        repository: PhotoPermissionRepository,
    ): GetRequestablePermissionsUseCase = GetRequestablePermissionsUseCase(repository)

    @Provides
    fun provideObserveDevicePhotosUseCase(
        repository: MediaGalleryRepository,
    ): ObserveDevicePhotosUseCase = ObserveDevicePhotosUseCase(repository)
}