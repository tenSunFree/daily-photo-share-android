package com.sun.daily_photo_share_android.core.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OffsetPagingTest {

    private val data = (0 until 300).toList()
    private fun query(window: OffsetWindow) = data.drop(window.offset).take(window.limit)

    /** Loads like Paging does: one refresh, then prepends and appends until both ends are reached. */
    private fun loadEverything(refreshKey: Int?, initialLoadSize: Int, pageSize: Int): List<Int> {
        val first = OffsetPaging.window(LoadDirection.Refresh, refreshKey, initialLoadSize)
        val firstItems = query(first)
        val pages = ArrayDeque<List<Int>>().apply { add(firstItems) }

        var prev = OffsetPaging.prevKey(first)
        while (prev != null) {
            val window = OffsetPaging.window(LoadDirection.Prepend, prev, pageSize)
            pages.addFirst(query(window))
            prev = OffsetPaging.prevKey(window)
        }

        var next = OffsetPaging.nextKey(first, firstItems.size)
        while (next != null) {
            val window = OffsetPaging.window(LoadDirection.Append, next, pageSize)
            val items = query(window)
            pages.addLast(items)
            next = OffsetPaging.nextKey(window, items.size)
        }
        return pages.flatten()
    }

    @Test
    fun loadsEverything_fromTheTop() {
        assertEquals(data, loadEverything(refreshKey = null, initialLoadSize = 120, pageSize = 60))
    }

    @Test
    fun refreshFromTheMiddle_hasNoGapsOrDuplicates_whenAligned() {
        assertEquals(data, loadEverything(refreshKey = 120, initialLoadSize = 120, pageSize = 60))
    }

    @Test
    fun refreshFromTheMiddle_hasNoGapsOrDuplicates_whenNotAligned() {
        assertEquals(data, loadEverything(refreshKey = 130, initialLoadSize = 120, pageSize = 60))
    }

    @Test
    fun refreshFromTheMiddle_withDefaultInitialLoadSize() {
        assertEquals(data, loadEverything(refreshKey = 77, initialLoadSize = 180, pageSize = 60))
    }

    @Test
    fun prepend_neverReadsPastThePageItPrecedes() {
        val window = OffsetPaging.window(LoadDirection.Prepend, key = 40, loadSize = 60)
        assertEquals(OffsetWindow(offset = 0, limit = 40), window)
    }

    @Test
    fun keys_atTheEdges() {
        assertNull(OffsetPaging.prevKey(OffsetWindow(0, 60)))
        assertNull(OffsetPaging.nextKey(OffsetWindow(240, 60), loadedCount = 59))
        assertEquals(300, OffsetPaging.nextKey(OffsetWindow(240, 60), loadedCount = 60))
    }

    @Test
    fun pageStart_isRecoveredFromPrevKey() {
        assertEquals(0, OffsetPaging.pageStart(prevKey = null))
        assertEquals(130, OffsetPaging.pageStart(prevKey = 130))
    }
}