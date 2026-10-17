package com.sun.daily_photo_share_android.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShareRecordEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class DailyDatabase : RoomDatabase() {
    abstract fun shareRecordDao(): ShareRecordDao
}