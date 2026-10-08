package com.sun.daily_photo_share_android.feature.gallery

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.sun.daily_photo_share_android.core.share.ShareIntents

/** Connects [GalleryViewModel] to [GalleryScreen]: state, permissions, Settings, back, sharing. */
@Composable
fun GalleryRoute(
    startInSelectionMode: Boolean,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val photos = viewModel.photos.collectAsLazyPagingItems()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Idempotent, so re-sending after a configuration change or process death is harmless.
    LaunchedEffect(startInSelectionMode) {
        if (startInSelectionMode) viewModel.onIntent(GalleryIntent.SelectionModeRequested)
    }

    // Back clears a selection first; with nothing selected, back navigates as usual.
    BackHandler(enabled = !state.selection.isEmpty) {
        viewModel.onIntent(GalleryIntent.ClearSelectionClicked)
    }

    // The permission dialog pauses and resumes this Activity, so the ON_RESUME check below
    // already picks up the result. Checking here too would restart the Partial query twice.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { }

    // Covers the first display, the permission dialog, changes made in Settings,
    // and a changed partial selection.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(GalleryIntent.CheckPermission)
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is GalleryEffect.RequestPermissions ->
                        permissionLauncher.launch(effect.permissions.toTypedArray())

                    GalleryEffect.OpenAppSettings ->
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            ),
                        )

                    is GalleryEffect.LaunchShare -> {
                        if (effect.skippedCount > 0) {
                            Toast.makeText(
                                context,
                                context.getString(
                                    R.string.gallery_share_skipped,
                                    effect.skippedCount
                                ),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        val intent = ShareIntents.create(
                            request = effect.request,
                            chooserTitle = context.getString(R.string.gallery_share_chooser_title),
                        )
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // LINE is missing or disabled; the screen offers the system share sheet.
                            viewModel.onIntent(GalleryIntent.ShareTargetUnavailable(effect.request))
                        }
                    }
                }
            }
        }
    }

    GalleryScreen(
        state = state,
        photos = photos,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}