package com.sun.daily_photo_share_android.core.media

import androidx.paging.PagingSource
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OffsetPagingSourceTest {

    private val data = (0 until 100).toList()

    private val source = OffsetPagingSource { offset, limit -> data.drop(offset).take(limit) }

    @Test
    fun refresh_fromTheTop_returnsFirstPageWithKeys() = runTest {
        val result = source.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 40, placeholdersEnabled = false))

        val page = result as PagingSource.LoadResult.Page
        assertEquals(data.take(40), page.data)
        assertNull(page.prevKey)
        assertEquals(40, page.nextKey)
    }

    @Test
    fun queryFailure_becomesLoadResultError() = runTest {
        val failing = OffsetPagingSource<Int> { _, _ -> throw IOException("boom") }

        val result = failing.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 40, placeholdersEnabled = false))

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun cancellation_isRethrown_notTurnedIntoAnError() = runTest {
        val cancelled = OffsetPagingSource<Int> { _, _ -> throw CancellationException("cancelled") }

        var rethrown = false
        try {
            cancelled.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 40, placeholdersEnabled = false))
        } catch (e: CancellationException) {
            rethrown = true
        }

        assertTrue(rethrown)
    }
}