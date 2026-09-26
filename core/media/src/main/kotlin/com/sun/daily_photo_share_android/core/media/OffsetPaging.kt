package com.sun.daily_photo_share_android.core.media

internal enum class LoadDirection { Refresh, Prepend, Append }

/** The rows [offset, offset + limit) to query for one page. */
internal data class OffsetWindow(val offset: Int, val limit: Int)

/**
 * Offset paging where a key means "start" for refresh/append and "end (exclusive)" for prepend.
 * Every page is then exactly adjacent to its neighbour, whatever offset a refresh starts from,
 * so pages never overlap (duplicate grid keys) and never leave gaps.
 */
internal object OffsetPaging {

    fun window(direction: LoadDirection, key: Int?, loadSize: Int): OffsetWindow =
        when (direction) {
            LoadDirection.Refresh, LoadDirection.Append ->
                OffsetWindow(offset = (key ?: 0).coerceAtLeast(0), limit = loadSize)

            LoadDirection.Prepend -> {
                val end = (key ?: 0).coerceAtLeast(0)
                val start = (end - loadSize).coerceAtLeast(0)
                OffsetWindow(offset = start, limit = end - start)
            }
        }

    /** Key for loading the rows before this page: this page's start, or null at the top. */
    fun prevKey(window: OffsetWindow): Int? = window.offset.takeIf { it > 0 }

    /** Key for loading the rows after this page, or null when fewer rows came back than asked. */
    fun nextKey(window: OffsetWindow, loadedCount: Int): Int? =
        if (loadedCount < window.limit) null else window.offset + loadedCount

    /** A page's own start offset, recovered from its prevKey. */
    fun pageStart(prevKey: Int?): Int = prevKey ?: 0
}