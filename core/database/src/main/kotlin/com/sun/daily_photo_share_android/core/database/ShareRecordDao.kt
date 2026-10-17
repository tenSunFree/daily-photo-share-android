package com.sun.daily_photo_share_android.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ShareRecordDao {

    @Insert
    suspend fun insert(record: ShareRecordEntity): Long

    /** Newest by time; id breaks a tie within the same millisecond. */
    @Query(
        """
        SELECT * FROM share_records
        ORDER BY handedOffAtEpochMillis DESC, id DESC
        LIMIT 1
        """,
    )
    fun observeLatest(): Flow<ShareRecordEntity?>
}