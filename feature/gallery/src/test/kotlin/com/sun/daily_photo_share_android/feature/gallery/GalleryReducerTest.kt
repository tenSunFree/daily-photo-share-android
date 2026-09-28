package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GalleryReducerTest {

    private val a = MediaUri("content://media/external/images/media/1")
    private val b = MediaUri("content://media/external/images/media/2")
    private val c = MediaUri("content://media/external/images/media/3")

    private fun reduce(state: GalleryUiState, vararg mutations: GalleryMutation): GalleryUiState =
        mutations.fold(state) { current, mutation -> GalleryReducer.reduce(current, mutation) }

    private fun checked(state: PhotoPermissionState) = GalleryMutation.PermissionChecked(state)
    private val full =
        GalleryUiState(permissionState = PhotoPermissionState.Full, accessRevision = 1)

    @Test
    fun initialState_isUnchecked_andNotSelecting() {
        val state = GalleryUiState()
        assertNull(state.permissionState)
        assertEquals(0, state.accessRevision)
        assertTrue(state.selection.isEmpty)
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun firstCheck_setsStateAndBumpsRevision() {
        val result = reduce(GalleryUiState(), checked(PhotoPermissionState.Full))
        assertEquals(PhotoPermissionState.Full, result.permissionState)
        assertEquals(1, result.accessRevision)
    }

    @Test
    fun fullToFull_keepsRevision_soTheGridIsNotReloadedOnEveryResume() {
        val result = reduce(full.copy(accessRevision = 3), checked(PhotoPermissionState.Full))
        assertEquals(3, result.accessRevision)
    }

    @Test
    fun partialToPartial_bumpsRevision_becauseTheSelectedSetMayHaveChanged() {
        val state = GalleryUiState(PhotoPermissionState.Partial, accessRevision = 3)
        val result = reduce(state, checked(PhotoPermissionState.Partial))
        assertEquals(4, result.accessRevision)
    }

    @Test
    fun stateChange_bumpsRevision() {
        val result = reduce(full.copy(accessRevision = 3), checked(PhotoPermissionState.Denied))
        assertEquals(PhotoPermissionState.Denied, result.permissionState)
        assertEquals(4, result.accessRevision)
    }

    @Test
    fun deniedToDenied_keepsRevision() {
        val state = GalleryUiState(PhotoPermissionState.Denied, accessRevision = 2)
        val result = reduce(state, checked(PhotoPermissionState.Denied))
        assertEquals(2, result.accessRevision)
    }

    @Test
    fun tap_outsideSelectionMode_changesNothing() {
        val result = reduce(full, GalleryMutation.PhotoTapped(a))
        assertEquals(full, result)
    }

    @Test
    fun longPress_entersSelectionMode_andSelectsThePhoto() {
        val result = reduce(full, GalleryMutation.PhotoLongPressed(a))
        assertTrue(result.isSelectionMode)
        assertEquals(listOf(a), result.selection.items)
    }

    @Test
    fun longPress_onAnAlreadySelectedPhoto_keepsItSelected() {
        val result =
            reduce(full, GalleryMutation.PhotoLongPressed(a), GalleryMutation.PhotoLongPressed(a))
        assertEquals(listOf(a), result.selection.items)
    }

    @Test
    fun tap_inSelectionMode_addsPhotosInShareOrder() {
        val result = reduce(
            full,
            GalleryMutation.PhotoLongPressed(a),
            GalleryMutation.PhotoTapped(b),
            GalleryMutation.PhotoTapped(c),
        )
        assertEquals(listOf(a, b, c), result.selection.items)
        assertEquals(2, result.selection.orderOf(b))
    }

    @Test
    fun tap_onASelectedPhoto_removesIt_andRenumbersTheRest() {
        val result = reduce(
            full,
            GalleryMutation.PhotoLongPressed(a),
            GalleryMutation.PhotoTapped(b),
            GalleryMutation.PhotoTapped(c),
            GalleryMutation.PhotoTapped(b),
        )
        assertEquals(listOf(a, c), result.selection.items)
        assertEquals(2, result.selection.orderOf(c))
    }

    @Test
    fun deselectingTheLastPhoto_leavesSelectionMode() {
        val result =
            reduce(full, GalleryMutation.PhotoLongPressed(a), GalleryMutation.PhotoTapped(a))
        assertTrue(result.selection.isEmpty)
        assertFalse(result.isSelectionMode)
    }

    @Test
    fun clearSelection_leavesSelectionMode() {
        val result = reduce(
            full,
            GalleryMutation.PhotoLongPressed(a),
            GalleryMutation.PhotoTapped(b),
            GalleryMutation.SelectionCleared,
        )
        assertTrue(result.selection.isEmpty)
        assertFalse(result.isSelectionMode)
    }

    @Test
    fun launchedForSelection_staysInSelectionMode_evenWhenEmpty() {
        val launched = reduce(full, GalleryMutation.SelectionModeRequested)
        assertTrue(launched.isSelectionMode)
        val afterClear =
            reduce(launched, GalleryMutation.PhotoTapped(a), GalleryMutation.SelectionCleared)
        assertTrue(afterClear.selection.isEmpty)
        assertTrue(afterClear.isSelectionMode)
    }

    @Test
    fun launchedForSelection_tapSelectsDirectly() {
        val result =
            reduce(full, GalleryMutation.SelectionModeRequested, GalleryMutation.PhotoTapped(a))
        assertEquals(listOf(a), result.selection.items)
    }

    @Test
    fun selectionModeRequested_isIdempotent() {
        val once =
            reduce(full, GalleryMutation.SelectionModeRequested, GalleryMutation.PhotoTapped(a))
        val twice = reduce(once, GalleryMutation.SelectionModeRequested)
        assertEquals(once, twice)
    }

    @Test
    fun permissionDenied_clearsTheSelection() {
        val selecting = reduce(full, GalleryMutation.PhotoLongPressed(a))
        val result = reduce(selecting, checked(PhotoPermissionState.Denied))
        assertTrue(result.selection.isEmpty)
    }

    @Test
    fun permissionPartial_keepsTheSelection() {
        val selecting = reduce(full, GalleryMutation.PhotoLongPressed(a))
        val result = reduce(selecting, checked(PhotoPermissionState.Partial))
        assertEquals(listOf(a), result.selection.items)
    }

    @Test
    fun selectionRestored_keepsOrder_andDropsDuplicates() {
        val result = reduce(full, GalleryMutation.SelectionRestored(listOf(b, a, b)))
        assertEquals(listOf(b, a), result.selection.items)
        assertTrue(result.isSelectionMode)
    }

    @Test
    fun selectionChanges_neverTouchTheAccessRevision() {
        val result = reduce(
            full,
            GalleryMutation.PhotoLongPressed(a),
            GalleryMutation.PhotoTapped(b),
            GalleryMutation.SelectionCleared,
        )
        assertEquals(full.accessRevision, result.accessRevision)
    }
}