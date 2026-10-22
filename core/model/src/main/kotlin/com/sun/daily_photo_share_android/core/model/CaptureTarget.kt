package com.sun.daily_photo_share_android.core.model

import java.time.Instant

data class CaptureTarget(
    val displayName: String,
    val mimeType: String,
    val relativePath: String,
    val capturedAt: Instant,
)