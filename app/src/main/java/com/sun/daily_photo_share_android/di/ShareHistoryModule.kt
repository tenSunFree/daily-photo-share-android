package com.sun.daily_photo_share_android.di

import com.sun.daily_photo_share_android.core.domain.RecordShareHandOffUseCase
import com.sun.daily_photo_share_android.core.domain.ShareHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

/** Constructs the framework-free share history use cases. The repository is bound by :core:database. */
@Module
@InstallIn(SingletonComponent::class)
object ShareHistoryModule {

    @Provides
    fun provideRecordShareHandOffUseCase(
        repository: ShareHistoryRepository,
        clock: Clock,
    ): RecordShareHandOffUseCase = RecordShareHandOffUseCase(repository, clock)
}