package com.sun.daily_photo_share_android.di

import com.sun.daily_photo_share_android.core.domain.PrepareCaptureUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
internal object CameraModule {

    @Provides
    fun providePrepareCaptureUseCase(clock: Clock): PrepareCaptureUseCase =
        PrepareCaptureUseCase(clock)
}