package com.sun.daily_photo_share_android.feature.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation

@Composable
fun CameraRoute(
    onBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalResources.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context.findActivity()

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    val preview = remember {
        Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequest = request }
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    fun observePermission(granted: Boolean) {
        viewModel.onIntent(
            CameraIntent.PermissionObserved(
                granted = granted,
                shouldShowRationale = activity?.shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA,
                ) == true,
            ),
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> observePermission(granted) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        observePermission(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED,
        )
    }

    // PreviewView is not used, so keep the use cases' rotation in sync ourselves.
    val configuration = LocalConfiguration.current
    val view = LocalView.current
    LaunchedEffect(configuration) {
        val rotation = view.display?.rotation ?: Surface.ROTATION_0
        preview.targetRotation = rotation
        imageCapture.targetRotation = rotation
    }

    // Bind while permission is granted; the lifecycle owner opens and closes the camera.
    LaunchedEffect(state.permissionState, lifecycleOwner) {
        if (state.permissionState != CameraPermissionState.Granted) {
            surfaceRequest = null
            return@LaunchedEffect
        }

        viewModel.onIntent(CameraIntent.CameraStarting)

        val provider = try {
            ProcessCameraProvider.awaitInstance(context.applicationContext)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            viewModel.onIntent(CameraIntent.CameraUnavailable)
            return@LaunchedEffect
        }

        try {
            val selector = when {
                provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ->
                    CameraSelector.DEFAULT_BACK_CAMERA
                provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ->
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else -> null
            }
            if (selector == null) {
                viewModel.onIntent(CameraIntent.CameraUnavailable)
                return@LaunchedEffect
            }

            provider.unbind(preview, imageCapture)
            provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            viewModel.onIntent(CameraIntent.CameraReady)

            awaitCancellation()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            viewModel.onIntent(CameraIntent.CameraUnavailable)
        } finally {
            provider.unbind(preview, imageCapture)
            surfaceRequest = null
        }
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    CameraEffect.RequestPermission ->
                        permissionLauncher.launch(Manifest.permission.CAMERA)

                    CameraEffect.OpenSettings ->
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                "package:${context.packageName}".toUri(),
                            ),
                        )

                    is CameraEffect.CapturePhoto ->
                        imageCapture.captureToMediaStore(
                            context = context,
                            target = effect.target,
                            onSaved = { viewModel.onIntent(CameraIntent.CaptureSucceeded) },
                            onFailed = { viewModel.onIntent(CameraIntent.CaptureFailed) },
                        )

                    CameraEffect.ShowCaptureFailed ->
                        Toast.makeText(
                            context,
                            resources.getString(R.string.camera_capture_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    CameraScreen(
        state = state,
        surfaceRequest = surfaceRequest,
        onBack = onBack,
        onIntent = viewModel::onIntent,
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}