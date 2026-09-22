package com.sun.daily_photo_share_android.di

import com.sun.daily_photo_share_android.core.domain.TodaySummaryRepository
import com.sun.daily_photo_share_android.core.model.DailyShareMediaNaming
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareTarget
import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * In-memory data used to exercise the Today screen without MediaStore.
 * A MediaStore-backed repository replaces it; the numbers shown are not real.
 */
class SampleTodaySummaryRepository @Inject constructor() : TodaySummaryRepository {

    override fun observeSummary(date: LocalDate): Flow<TodaySummary> = flowOf(sample(date))

    private fun sample(date: LocalDate): TodaySummary {
        val photos = listOf(
            photo(4, date, 10, 42),
            photo(3, date, 10, 40),
            photo(2, date, 10, 38),
            photo(1, date, 10, 35),
        )
        return TodaySummary(
            date = date,
            photoCount = 23,
            recentPhotos = photos,
            lastCapturedAt = photos.first().takenAt,
            lastShare = ShareRecord(
                id = 1,
                handedOffAt = date.atTime(10, 31).atZone(ZoneId.systemDefault()).toInstant(),
                photoCount = 8,
                target = ShareTarget.LINE,
            ),
        )
    }

    private fun photo(id: Long, date: LocalDate, hour: Int, minute: Int): MediaPhoto {
        val dateTime = date.atTime(hour, minute)
        val takenAt = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        return MediaPhoto(
            id = id,
            uri = MediaUri("content://media/external/images/media/$id"),
            displayName = DailyShareMediaNaming.fileNameFor(dateTime),
            mimeType = "image/jpeg",
            takenAt = takenAt,
            dateAdded = takenAt,
            width = 4000,
            height = 3000,
            relativePath = DailyShareMediaNaming.relativePathFor(date),
            bucketId = 1,
            bucketName = date.toString(),
        )
    }
}