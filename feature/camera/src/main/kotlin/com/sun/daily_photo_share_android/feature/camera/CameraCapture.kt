package com.sun.daily_photo_share_android.feature.camera

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.sun.daily_photo_share_android.core.model.CaptureTarget

internal fun ImageCapture.captureToMediaStore(
    context: Context,
    target: CaptureTarget,
    onSaved: () -> Unit,
    onFailed: () -> Unit,
) {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, target.displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, target.mimeType)
        put(MediaStore.Images.Media.RELATIVE_PATH, target.relativePath)
        put(MediaStore.Images.Media.DATE_TAKEN, target.capturedAt.toEpochMilli())
    }

    val options = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values,
    ).build()

    takePicture(
        options,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                if (outputFileResults.savedUri != null) onSaved() else onFailed()
            }

            override fun onError(exception: ImageCaptureException) {
                onFailed()
            }
        },
    )
}