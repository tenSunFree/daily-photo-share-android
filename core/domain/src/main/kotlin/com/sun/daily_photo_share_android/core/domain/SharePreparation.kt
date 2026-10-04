package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRequest

sealed interface SharePreparation {
    /** At least one photo is readable; [unreadable] were skipped. */
    data class Ready(val request: ShareRequest, val unreadable: List<MediaUri>) : SharePreparation

    /** Every selected photo has become unreadable (deleted, or removed from partial access). */
    data class NothingReadable(val unreadable: List<MediaUri>) : SharePreparation

    data object NothingSelected : SharePreparation
}