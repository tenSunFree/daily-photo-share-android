package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget

class PrepareShareUseCase(
    private val readabilityChecker: MediaReadabilityChecker,
) {
    /** Share order always follows [selection], whatever order the checker returns. */
    suspend operator fun invoke(selection: List<MediaUri>, target: ShareTarget): SharePreparation {
        if (selection.isEmpty()) return SharePreparation.NothingSelected
        val readable = readabilityChecker.readable(selection).toSet()
        val (kept, unreadable) = selection.partition { it in readable }
        return if (kept.isEmpty()) {
            SharePreparation.NothingReadable(unreadable)
        } else {
            SharePreparation.Ready(ShareRequest(kept, target), unreadable)
        }
    }
}