package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.CaptureTarget
import com.sun.daily_photo_share_android.core.model.DailyShareMediaNaming
import java.time.Clock
import java.time.LocalDateTime

class PrepareCaptureUseCase(
    private val clock: Clock,
) {
    operator fun invoke(): CaptureTarget {
        val instant = clock.instant()
        val capturedAt = LocalDateTime.ofInstant(instant, clock.zone)
        return CaptureTarget(
            displayName = DailyShareMediaNaming.fileNameFor(capturedAt),
            mimeType = "image/jpeg",
            relativePath = DailyShareMediaNaming.relativePathFor(capturedAt.toLocalDate()),
            capturedAt = instant,
        )
    }
}