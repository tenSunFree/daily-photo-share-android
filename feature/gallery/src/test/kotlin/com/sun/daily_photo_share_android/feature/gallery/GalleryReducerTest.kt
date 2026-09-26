package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GalleryReducerTest {

    private fun checked(state: PhotoPermissionState) = GalleryMutation.PermissionChecked(state)

    @Test
    fun initialState_isUnchecked() {
        val state = GalleryUiState()

        assertNull(state.permissionState)
        assertEquals(0, state.accessRevision)
    }

    @Test
    fun firstCheck_setsStateAndBumpsRevision() {
        val result = GalleryReducer.reduce(GalleryUiState(), checked(PhotoPermissionState.Full))

        assertEquals(PhotoPermissionState.Full, result.permissionState)
        assertEquals(1, result.accessRevision)
    }

    @Test
    fun fullToFull_keepsRevision_soTheGridIsNotReloadedOnEveryResume() {
        val state = GalleryUiState(PhotoPermissionState.Full, accessRevision = 3)

        val result = GalleryReducer.reduce(state, checked(PhotoPermissionState.Full))

        assertEquals(3, result.accessRevision)
    }

    @Test
    fun partialToPartial_bumpsRevision_becauseTheSelectedSetMayHaveChanged() {
        val state = GalleryUiState(PhotoPermissionState.Partial, accessRevision = 3)

        val result = GalleryReducer.reduce(state, checked(PhotoPermissionState.Partial))

        assertEquals(PhotoPermissionState.Partial, result.permissionState)
        assertEquals(4, result.accessRevision)
    }

    @Test
    fun stateChange_bumpsRevision() {
        val state = GalleryUiState(PhotoPermissionState.Full, accessRevision = 3)

        val result = GalleryReducer.reduce(state, checked(PhotoPermissionState.Denied))

        assertEquals(PhotoPermissionState.Denied, result.permissionState)
        assertEquals(4, result.accessRevision)
    }

    @Test
    fun deniedToDenied_keepsRevision() {
        val state = GalleryUiState(PhotoPermissionState.Denied, accessRevision = 2)

        val result = GalleryReducer.reduce(state, checked(PhotoPermissionState.Denied))

        assertEquals(2, result.accessRevision)
    }
}