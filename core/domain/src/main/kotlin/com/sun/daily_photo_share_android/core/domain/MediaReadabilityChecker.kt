package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.MediaUri

interface MediaReadabilityChecker {
    /** The subset of [uris] the app can still read right now. */
    suspend fun readable(uris: List<MediaUri>): List<MediaUri>
}