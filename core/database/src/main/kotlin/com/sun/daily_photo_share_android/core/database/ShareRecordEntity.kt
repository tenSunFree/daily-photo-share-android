package com.sun.daily_photo_share_android.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "share_records")
internal data class ShareRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val handedOffAtEpochMillis: Long,
    val photoCount: Int,
    val target: String,
    val status: String,
)