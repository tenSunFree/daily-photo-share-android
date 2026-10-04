package com.sun.daily_photo_share_android.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ShareRequestTest {

    @Test
    fun keepsUrisInOrder() {
        val a = MediaUri("content://media/external/images/media/1")
        val b = MediaUri("content://media/external/images/media/2")
        assertEquals(listOf(b, a), ShareRequest(listOf(b, a), ShareTarget.LINE).uris)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsAnEmptyList() {
        ShareRequest(emptyList(), ShareTarget.LINE)
    }
}