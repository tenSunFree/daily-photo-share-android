package com.sun.daily_photo_share_android.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoSelectionTest {

    private val a = MediaUri("content://media/external/images/media/1")
    private val b = MediaUri("content://media/external/images/media/2")
    private val c = MediaUri("content://media/external/images/media/3")

    @Test
    fun toggle_addsItemsInOrder() {
        val selection = PhotoSelection().toggle(a).toggle(b).toggle(c)
        assertEquals(listOf(a, b, c), selection.items)
        assertEquals(1, selection.orderOf(a))
        assertEquals(2, selection.orderOf(b))
        assertEquals(3, selection.orderOf(c))
    }

    @Test
    fun toggle_removeRenumbersRemainingItems() {
        val selection = PhotoSelection().toggle(a).toggle(b).toggle(c).toggle(b)
        assertEquals(listOf(a, c), selection.items)
        assertEquals(2, selection.orderOf(c)) // c moves from ③ to ②
        assertNull(selection.orderOf(b))
        assertFalse(b in selection)
    }

    @Test
    fun toggle_doesNotMutateOriginal() {
        val original = PhotoSelection().toggle(a)
        original.toggle(b)
        assertEquals(listOf(a), original.items)
    }

    @Test
    fun addAll_skipsDuplicatesAndKeepsExistingOrder() {
        val selection = PhotoSelection().toggle(b).addAll(listOf(a, b, c))
        assertEquals(listOf(b, a, c), selection.items)
    }

    @Test
    fun clear_removesEverything() {
        val selection = PhotoSelection().toggle(a).toggle(b).clear()
        assertTrue(selection.isEmpty)
        assertEquals(0, selection.size)
    }
}