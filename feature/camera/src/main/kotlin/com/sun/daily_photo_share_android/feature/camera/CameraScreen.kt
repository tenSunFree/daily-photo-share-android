package com.sun.daily_photo_share_android.feature.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    state: CameraUiState,
    surfaceRequest: SurfaceRequest?,
    onBack: () -> Unit,
    onIntent: (CameraIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Black)) {
        when (state.permissionState) {
            null -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            CameraPermissionState.Requestable -> PermissionContent(
                title = stringResource(R.string.camera_permission_title),
                description = stringResource(R.string.camera_permission_description),
                actionText = stringResource(R.string.camera_permission_allow),
                onAction = { onIntent(CameraIntent.RequestPermissionClicked) },
                modifier = Modifier.align(Alignment.Center),
            )

            CameraPermissionState.NeedsSettings -> PermissionContent(
                title = stringResource(R.string.camera_settings_title),
                description = stringResource(R.string.camera_settings_description),
                actionText = stringResource(R.string.camera_open_settings),
                onAction = { onIntent(CameraIntent.OpenSettingsClicked) },
                modifier = Modifier.align(Alignment.Center),
            )

            CameraPermissionState.Granted -> {
                if (surfaceRequest != null) {
                    CameraXViewfinder(
                        surfaceRequest = surfaceRequest,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                when (state.cameraState) {
                    CameraRuntimeState.Starting ->
                        CircularProgressIndicator(Modifier.align(Alignment.Center))

                    CameraRuntimeState.Unavailable -> Text(
                        text = stringResource(R.string.camera_unavailable),
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    CameraRuntimeState.Idle, CameraRuntimeState.Ready -> Unit
                }

                ShutterButton(
                    enabled = state.canCapture,
                    isCapturing = state.isCapturing,
                    onClick = { onIntent(CameraIntent.CaptureClicked) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(bottom = 24.dp),
                )
            }
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(8.dp),
        ) {
            Text(text = stringResource(R.string.camera_back), color = Color.White)
        }

        if (state.savedCount > 0) {
            Text(
                text = stringResource(R.string.camera_saved_count, state.savedCount),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.camera_shutter)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = modifier
            .size(80.dp)
            .border(width = 4.dp, color = Color.White, shape = CircleShape)
            .semantics { contentDescription = description },
    ) {
        if (isCapturing) {
            CircularProgressIndicator(Modifier.size(24.dp))
        }
    }
}

@Composable
private fun PermissionContent(
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(text = description, color = Color.White)
        Button(onClick = onAction) { Text(actionText) }
    }
}