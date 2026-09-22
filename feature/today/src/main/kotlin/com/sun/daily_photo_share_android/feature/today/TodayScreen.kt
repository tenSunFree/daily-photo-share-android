package com.sun.daily_photo_share_android.feature.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sun.daily_photo_share_android.core.designsystem.DailyIcons
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme
import com.sun.daily_photo_share_android.core.designsystem.components.DailyButton
import com.sun.daily_photo_share_android.core.designsystem.components.DailyTonalButton
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.ShareRecord
import com.sun.daily_photo_share_android.core.model.ShareTarget
import com.sun.daily_photo_share_android.core.model.TodaySummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val RECENT_PHOTO_SLOTS = 4

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun formatTime(instant: Instant): String =
    timeFormatter.withZone(ZoneId.systemDefault()).format(instant)

/** UiState in, Intent out. Holds no state of its own. */
@Composable
fun TodayScreen(
    state: TodayUiState,
    onIntent: (TodayIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary = state.summary
    when {
        summary != null -> TodayContent(summary, onIntent, modifier)
        state.error != null -> ErrorContent({ onIntent(TodayIntent.Refresh) }, modifier)
        else -> LoadingContent(modifier)
    }
}

@Composable
private fun TodayContent(
    summary: TodaySummary,
    onIntent: (TodayIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.md),
    ) {
        Text(
            text = summary.date.toString(),
            style = MaterialTheme.typography.headlineMedium,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(DailyTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.xs),
            ) {
                Text(
                    text = if (summary.photoCount == 0) {
                        stringResource(R.string.today_no_photos)
                    } else {
                        stringResource(R.string.today_photo_count, summary.photoCount)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                summary.lastCapturedAt?.let { capturedAt ->
                    Text(
                        text = stringResource(R.string.today_last_captured, formatTime(capturedAt)),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        if (summary.recentPhotos.isNotEmpty()) {
            RecentPhotosRow(photos = summary.recentPhotos)
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = lastShareText(summary.lastShare),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(DailyTheme.spacing.md),
            )
        }

        DailyButton(
            text = stringResource(R.string.today_action_capture),
            onClick = { onIntent(TodayIntent.CaptureClicked) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(DailyIcons.Camera, contentDescription = null) },
        )
        DailyTonalButton(
            text = stringResource(R.string.today_action_share_line),
            onClick = { onIntent(TodayIntent.SelectPhotosForLineClicked) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(DailyIcons.Share, contentDescription = null) },
        )
    }
}

@Composable
private fun lastShareText(lastShare: ShareRecord?): String {
    if (lastShare == null) return stringResource(R.string.today_no_share)
    val time = formatTime(lastShare.handedOffAt)
    return when (lastShare.target) {
        ShareTarget.LINE -> stringResource(R.string.today_last_share_line, time, lastShare.photoCount)
        ShareTarget.SYSTEM_SHEET -> stringResource(R.string.today_last_share_other, time, lastShare.photoCount)
    }
}

/** Placeholder tiles showing the capture time (no image loading). */
@Composable
private fun RecentPhotosRow(photos: List<MediaPhoto>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DailyTheme.spacing.sm),
    ) {
        repeat(RECENT_PHOTO_SLOTS) { index ->
            val photo = photos.getOrNull(index)
            if (photo != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = formatTime(photo.takenAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.today_error_load),
            style = MaterialTheme.typography.bodyLarge,
        )
        DailyButton(text = stringResource(R.string.today_action_retry), onClick = onRetry)
    }
}