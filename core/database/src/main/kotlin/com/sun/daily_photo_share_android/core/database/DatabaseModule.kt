package com.sun.daily_photo_share_android.core.database

import android.content.Context
import androidx.room.Room
import com.sun.daily_photo_share_android.core.domain.ShareHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DatabaseModule {

    @Binds
    abstract fun bindShareHistoryRepository(impl: RoomShareHistoryRepository): ShareHistoryRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): DailyDatabase =
            Room.databaseBuilder(context, DailyDatabase::class.java, "daily-share.db").build()

        @Provides
        fun provideShareRecordDao(database: DailyDatabase): ShareRecordDao =
            database.shareRecordDao()
    }
}