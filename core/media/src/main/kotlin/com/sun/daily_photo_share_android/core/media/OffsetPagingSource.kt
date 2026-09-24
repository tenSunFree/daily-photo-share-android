package com.sun.daily_photo_share_android.core.media

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.coroutines.cancellation.CancellationException

/** Generic offset-based PagingSource; no Android dependency, so it is unit-testable on the JVM. */
internal class OffsetPagingSource<T : Any>(
    private val query: suspend (offset: Int, limit: Int) -> List<T>,
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return OffsetPaging.pageStart(page.prevKey)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val window = OffsetPaging.window(params.direction(), params.key, params.loadSize)
        return try {
            val items = query(window.offset, window.limit)
            LoadResult.Page(
                data = items,
                prevKey = OffsetPaging.prevKey(window),
                nextKey = OffsetPaging.nextKey(window, items.size),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun LoadParams<Int>.direction(): LoadDirection = when (this) {
        is LoadParams.Refresh -> LoadDirection.Refresh
        is LoadParams.Append -> LoadDirection.Append
        is LoadParams.Prepend -> LoadDirection.Prepend
    }
}