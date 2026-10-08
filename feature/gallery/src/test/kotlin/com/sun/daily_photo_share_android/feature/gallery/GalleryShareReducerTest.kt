package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import com.sun.daily_photo_share_android.core.model.PhotoSelection
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GalleryShareReducerTest {

    private val a = MediaUri("content://media/external/images/media/1")
    private val b = MediaUri("content://media/external/images/media/2")
    private val c = MediaUri("content://media/external/images/media/3")

    private val selecting = GalleryUiState(
        permissionState = PhotoPermissionState.Full,
        accessRevision = 1,
        selection = PhotoSelection(listOf(a, b, c)),
    )

    private fun reduce(state: GalleryUiState, vararg mutations: GalleryMutation): GalleryUiState =
        mutations.fold(state) { current, mutation -> GalleryReducer.reduce(current, mutation) }

    @Test
    fun canShare_needsASelection() {
        assertFalse(GalleryUiState().canShare)
        assertTrue(selecting.canShare)
    }

    @Test
    fun preparation_blocksAnotherShare() {
        val result = reduce(selecting, GalleryMutation.SharePreparationStarted)

        assertTrue(result.isPreparingShare)
        assertFalse(result.canShare)
    }

    @Test
    fun preparationFinished_withEverythingReadable_keepsTheSelection() {
        val result = reduce(
            selecting,
            GalleryMutation.SharePreparationStarted,
            GalleryMutation.SharePreparationFinished(unreadable = emptyList(), problem = null),
        )

        assertFalse(result.isPreparingShare)
        assertEquals(listOf(a, b, c), result.selection.items)
        assertNull(result.shareProblem)
    }

    @Test
    fun unreadablePhotos_areRemovedFromTheSelection_andTheRestRenumbered() {
        val result = reduce(
            selecting,
            GalleryMutation.SharePreparationStarted,
            GalleryMutation.SharePreparationFinished(unreadable = listOf(b), problem = null),
        )

        assertEquals(listOf(a, c), result.selection.items)
        assertEquals(2, result.selection.orderOf(c))
    }

    @Test
    fun pruning_keepsPhotosTappedWhileTheCheckWasRunning() {
        val d = MediaUri("content://media/external/images/media/4")

        val result = reduce(
            selecting,
            GalleryMutation.SharePreparationStarted,
            GalleryMutation.PhotoTapped(d),
            GalleryMutation.SharePreparationFinished(unreadable = listOf(a), problem = null),
        )

        assertEquals(listOf(b, c, d), result.selection.items)
    }

    @Test
    fun nothingReadable_emptiesTheSelection_andShowsTheProblem() {
        val result = reduce(
            selecting,
            GalleryMutation.SharePreparationStarted,
            GalleryMutation.SharePreparationFinished(
                unreadable = listOf(a, b, c),
                problem = ShareProblem.NothingReadable,
            ),
        )

        assertTrue(result.selection.isEmpty)
        assertEquals(ShareProblem.NothingReadable, result.shareProblem)
        assertFalse(result.isSelectionMode)
    }

    @Test
    fun lineNotInstalled_isShown_andThenDismissed() {
        val request = ShareRequest(listOf(a, b), ShareTarget.LINE)

        val shown = reduce(
            selecting,
            GalleryMutation.ShareProblemShown(ShareProblem.LineNotInstalled(request))
        )
        assertEquals(ShareProblem.LineNotInstalled(request), shown.shareProblem)

        val dismissed = reduce(shown, GalleryMutation.ShareProblemDismissed)
        assertNull(dismissed.shareProblem)
        assertEquals(listOf(a, b, c), dismissed.selection.items)
    }

    @Test
    fun shareMutations_neverTouchTheAccessRevision() {
        val result = reduce(
            selecting,
            GalleryMutation.SharePreparationStarted,
            GalleryMutation.SharePreparationFinished(unreadable = listOf(a), problem = null),
            GalleryMutation.ShareProblemShown(ShareProblem.NothingReadable),
            GalleryMutation.ShareProblemDismissed,
        )

        assertEquals(selecting.accessRevision, result.accessRevision)
    }
}