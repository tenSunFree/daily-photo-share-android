package com.sun.daily_photo_share_android.core.model

/** Photos to hand to another app, in share order. Never empty. */
data class ShareRequest(
    val uris: List<MediaUri>,
    val target: ShareTarget,
) {
    init {
        require(uris.isNotEmpty()) { "A share request needs at least one photo." }
    }
}