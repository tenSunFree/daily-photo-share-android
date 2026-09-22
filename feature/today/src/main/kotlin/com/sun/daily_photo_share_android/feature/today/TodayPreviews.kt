package com.sun.daily_photo_share_android.feature.today

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme
import com.sun.daily_photo_share_android.core.model.DailyShareMediaNaming
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareTarget
import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.LocalDate
import java.time.ZoneId

private val previewDate: LocalDate = LocalDate.of(2026, 9, 19)

private fun previewPhoto(id: Long, hour: Int, minute: Int): MediaPhoto {
    val dateTime = previewDate.atTime(hour, minute)
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
        relativePath = DailyShareMediaNaming.relativePathFor(previewDate),
        bucketId = 1,
        bucketName = previewDate.toString(),
    )
}

private fun previewSummary(photoCount: Int): TodaySummary {
    val photos = if (photoCount == 0) {
        emptyList()
    } else {
        listOf(
            previewPhoto(4, 10, 42),
            previewPhoto(3, 10, 40),
            previewPhoto(2, 10, 38),
            previewPhoto(1, 10, 35),
        )
    }
    return TodaySummary(
        date = previewDate,
        photoCount = photoCount,
        recentPhotos = photos,
        lastCapturedAt = photos.firstOrNull()?.takenAt,
        lastShare = if (photoCount == 0) {
            null
        } else {
            ShareRecord(
                id = 1,
                handedOffAt = previewDate.atTime(10, 31).atZone(ZoneId.systemDefault()).toInstant(),
                photoCount = 8,
                target = ShareTarget.LINE,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TodayScreenLoadedPreview() {
    DailyTheme {
        TodayScreen(
            state = TodayUiState(isLoading = false, summary = previewSummary(23)),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayScreenEmptyPreview() {
    DailyTheme {
        TodayScreen(
            state = TodayUiState(isLoading = false, summary = previewSummary(0)),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayScreenErrorPreview() {
    DailyTheme {
        TodayScreen(
            state = TodayUiState(isLoading = false, error = TodayError.LoadFailed),
            onIntent = {},
        )
    }
}