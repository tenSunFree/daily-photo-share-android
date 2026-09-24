package com.sun.daily_photo_share_android.feature.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import java.time.Instant
import kotlinx.coroutines.flow.flowOf

private fun samplePhoto(id: Long) = MediaPhoto(
    id = id,
    uri = MediaUri("content://media/external/images/media/$id"),
    displayName = "IMG_$id.jpg",
    mimeType = "image/jpeg",
    takenAt = Instant.EPOCH,
    dateAdded = Instant.EPOCH,
    width = 4000,
    height = 3000,
    relativePath = "DCIM/Camera/",
    bucketId = 1,
    bucketName = "Camera",
)

@Composable
private fun previewItems(count: Int): LazyPagingItems<MediaPhoto> =
    remember(count) {
        flowOf(PagingData.from((1L..count).map(::samplePhoto)))
    }.collectAsLazyPagingItems()

@Preview(showBackground = true)
@Composable
private fun GalleryDeniedPreview() {
    DailyTheme {
        GalleryScreen(
            state = GalleryUiState(permissionState = PhotoPermissionState.Denied),
            photos = previewItems(0),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GalleryPartialPreview() {
    DailyTheme {
        GalleryScreen(
            state = GalleryUiState(permissionState = PhotoPermissionState.Partial),
            photos = previewItems(4),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GalleryFullPreview() {
    DailyTheme {
        GalleryScreen(
            state = GalleryUiState(permissionState = PhotoPermissionState.Full),
            photos = previewItems(12),
            onIntent = {},
        )
    }
}