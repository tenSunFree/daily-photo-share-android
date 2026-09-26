package com.sun.daily_photo_share_android.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme
import com.sun.daily_photo_share_android.core.designsystem.components.DailyButton
import com.sun.daily_photo_share_android.core.designsystem.components.DailyTonalButton
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

/** Photo keys are content URIs, so this key can never collide with a photo. */
private const val REFRESH_ERROR_KEY = "gallery-refresh-error"

/** UiState + paging items in, Intent out. Holds no state of its own. */
@Composable
fun GalleryScreen(
    state: GalleryUiState,
    photos: LazyPagingItems<MediaPhoto>,
    onIntent: (GalleryIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.permissionState) {
        // One frame before the first check: render nothing rather than a wrong state.
        null -> Box(modifier.fillMaxSize())

        PhotoPermissionState.Denied -> PermissionDeniedContent(
            onRequestClick = { onIntent(GalleryIntent.RequestPermissionClicked) },
            onOpenSettingsClick = { onIntent(GalleryIntent.OpenSettingsClicked) },
            modifier = modifier,
        )

        PhotoPermissionState.Partial -> Column(modifier = modifier.fillMaxSize()) {
            PartialAccessBanner(onManageClick = { onIntent(GalleryIntent.ManageSelectedPhotosClicked) })
            PhotoGrid(photos = photos, modifier = Modifier.fillMaxSize())
        }

        PhotoPermissionState.Full -> PhotoGrid(photos = photos, modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun PermissionDeniedContent(
    onRequestClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(
            DailyTheme.spacing.sm,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.gallery_permission_denied_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.gallery_permission_denied_message),
            style = MaterialTheme.typography.bodyMedium,
        )
        DailyButton(
            text = stringResource(R.string.gallery_permission_request_action),
            onClick = onRequestClick,
        )
        // After repeated denials the system no longer shows the dialog; Settings is the way back.
        DailyTonalButton(
            text = stringResource(R.string.gallery_open_settings_action),
            onClick = onOpenSettingsClick,
        )
    }
}

@Composable
private fun PartialAccessBanner(onManageClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(DailyTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.xs),
        ) {
            Text(
                text = stringResource(R.string.gallery_permission_partial_message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            DailyTonalButton(
                text = stringResource(R.string.gallery_manage_partial_access),
                onClick = onManageClick,
            )
        }
    }
}

@Composable
private fun PhotoGrid(photos: LazyPagingItems<MediaPhoto>, modifier: Modifier = Modifier) {
    val refresh = photos.loadState.refresh

    when {
        photos.itemCount == 0 && refresh is LoadState.Loading ->
            Box(modifier, contentAlignment = Alignment.Center) { CircularProgressIndicator() }

        photos.itemCount == 0 && refresh is LoadState.Error ->
            MessageWithAction(
                message = stringResource(R.string.gallery_error),
                actionText = stringResource(R.string.gallery_action_retry),
                onAction = photos::retry,
                modifier = modifier,
            )

        photos.itemCount == 0 ->
            Box(modifier, contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.gallery_empty))
            }

        else -> LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = DailyTheme.sizes.photoGridMinCell),
            modifier = modifier,
            contentPadding = PaddingValues(DailyTheme.spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(DailyTheme.spacing.xxs),
            verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.xxs),
        ) {
            // A failed refresh keeps the previous photos on screen; say so and offer a retry.
            if (refresh is LoadState.Error) {
                item(key = REFRESH_ERROR_KEY, span = { GridItemSpan(maxLineSpan) }) {
                    MessageWithAction(
                        message = stringResource(R.string.gallery_error),
                        actionText = stringResource(R.string.gallery_action_retry),
                        onAction = photos::retry,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            items(count = photos.itemCount, key = photos.itemKey { it.uri.value }) { index ->
                photos[index]?.let { photo -> PhotoTile(photo) }
            }

            when (photos.loadState.append) {
                is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DailyTheme.spacing.sm),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    MessageWithAction(
                        message = stringResource(R.string.gallery_error),
                        actionText = stringResource(R.string.gallery_action_retry),
                        onAction = photos::retry,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun MessageWithAction(
    message: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(
            DailyTheme.spacing.sm,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        DailyTonalButton(text = actionText, onClick = onAction)
    }
}

/** Coil sizes the decode to the tile's constraints, so full-resolution images are never loaded here. */
@Composable
private fun PhotoTile(photo: MediaPhoto, modifier: Modifier = Modifier) {
    AsyncImage(
        model = photo.uri.value,
        contentDescription = photo.displayName,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}