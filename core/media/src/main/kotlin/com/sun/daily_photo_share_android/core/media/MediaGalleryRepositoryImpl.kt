package com.sun.daily_photo_share_android.core.media

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sun.daily_photo_share_android.core.domain.MediaGalleryRepository
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal class MediaGalleryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSource: MediaStoreImageDataSource,
) : MediaGalleryRepository {

    /**
     * The ContentObserver lives exactly as long as this flow is collected: it is registered when
     * collection starts and unregistered in `finally` when collection is cancelled, so it can
     * never outlive the Pager. A MediaStore change invalidates the current PagingSource.
     */
    override fun observePhotos(): Flow<PagingData<MediaPhoto>> = flow {
        val sourceFactory = InvalidatingPagingSourceFactory {
            OffsetPagingSource(query = dataSource::queryPage)
        }
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                sourceFactory.invalidate()
            }
        }
        val resolver = context.contentResolver
        resolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        try {
            emitAll(Pager(config = pagingConfig, pagingSourceFactory = sourceFactory).flow)
        } finally {
            resolver.unregisterContentObserver(observer)
        }
    }

    private companion object {
        const val PAGE_SIZE = 60

        val pagingConfig = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE * 2,
            prefetchDistance = PAGE_SIZE / 2,
            enablePlaceholders = false,
        )
    }
}