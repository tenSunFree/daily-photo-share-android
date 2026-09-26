package com.sun.daily_photo_share_android.core.media

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** Reads one page of MediaStore.Images rows. Thin adapter: Cursor in, pure mapping out. */
internal class MediaStoreImageDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun queryPage(offset: Int, limit: Int): List<MediaPhoto> {
        if (limit <= 0) return emptyList()
        return withContext(Dispatchers.IO) { query(offset, limit) }
    }

    private fun query(offset: Int, limit: Int): List<MediaPhoto> {
        val queryArgs = Bundle().apply {
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID,
                ),
            )
            putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            )
            putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
        }

        // A null cursor means the provider failed; report it so Paging shows a retryable error
        // instead of an empty gallery.
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            queryArgs,
            null,
        ) ?: throw IOException("MediaStore query returned no cursor")

        return cursor.use {
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val takenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val addedIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val bucketIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val row = MediaStoreRow(
                        id = id,
                        displayName = cursor.getString(nameIndex),
                        mimeType = cursor.getString(mimeIndex),
                        dateTakenMillis = if (cursor.isNull(takenIndex)) null else cursor.getLong(
                            takenIndex
                        ),
                        dateAddedSeconds = cursor.getLong(addedIndex),
                        width = cursor.getInt(widthIndex),
                        height = cursor.getInt(heightIndex),
                        relativePath = cursor.getString(pathIndex),
                        bucketId = cursor.getLong(bucketIdIndex),
                        bucketDisplayName = cursor.getString(bucketNameIndex),
                    )
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    add(MediaStoreMapper.toMediaPhoto(row, MediaUri(uri.toString())))
                }
            }
        }
    }

    private companion object {
        val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
    }
}