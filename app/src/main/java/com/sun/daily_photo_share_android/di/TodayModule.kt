package com.sun.daily_photo_share_android.di

import com.sun.daily_photo_share_android.core.domain.DateProvider
import com.sun.daily_photo_share_android.core.domain.GetTodaySummaryUseCase
import com.sun.daily_photo_share_android.core.domain.SystemDateProvider
import com.sun.daily_photo_share_android.core.domain.TodaySummaryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Wires the framework-free :core:domain classes into Hilt.
 * :core:domain has no DI annotations, so its use cases are constructed here.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TodayModule {

    @Binds
    abstract fun bindTodaySummaryRepository(
        impl: SampleTodaySummaryRepository,
    ): TodaySummaryRepository

    companion object {
        @Provides
        fun provideDateProvider(): DateProvider = SystemDateProvider()

        @Provides
        fun provideGetTodaySummaryUseCase(
            repository: TodaySummaryRepository,
            dateProvider: DateProvider,
        ): GetTodaySummaryUseCase = GetTodaySummaryUseCase(repository, dateProvider)
    }
}