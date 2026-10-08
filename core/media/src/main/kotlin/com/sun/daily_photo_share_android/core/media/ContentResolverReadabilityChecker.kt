package com.sun.daily_photo_share_android.core.media

import android.content.Context
import android.provider.MediaStore
import com.sun.daily_photo_share_android.core.domain.MediaReadabilityChecker
import com.sun.daily_photo_share_android.core.model.MediaUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

/**
 * A URI is readable when a query for it returns a row. Deleted photos return no row, and photos
 * removed from Android 14+ partial access are filtered out by MediaStore or throw SecurityException.
 */
internal class ContentResolverReadabilityChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) : MediaReadabilityChecker {

    override suspend fun readable(uris: List<MediaUri>): List<MediaUri> =
        withContext(Dispatchers.IO) { uris.filter(::isReadable) }

    // Any provider failure counts as unreadable, so the share flow always finishes.
    private fun isReadable(uri: MediaUri): Boolean = try {
        context.contentResolver.query(
            uri.value.toUri(),
            arrayOf(MediaStore.MediaColumns._ID),
            null,
            null,
            null,
        )?.use { cursor -> cursor.moveToFirst() } ?: false
    } catch (e: RuntimeException) {
        false
    }
}