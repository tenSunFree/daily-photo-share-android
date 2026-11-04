package com.sun.daily_photo_share_android.feature.camera

import androidx.lifecycle.SavedStateHandle
import com.sun.daily_photo_share_android.core.domain.PrepareCaptureUseCase
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraViewModelTest {

    private val clock = Clock.fixed(
        Instant.parse("2026-10-20T16:30:12.123Z"),
        ZoneId.of("Asia/Taipei"),
    )

    private fun viewModel() = CameraViewModel(PrepareCaptureUseCase(clock), SavedStateHandle())

    private fun CameraViewModel.makeReady() {
        onIntent(CameraIntent.PermissionObserved(granted = true, shouldShowRationale = false))
        onIntent(CameraIntent.CameraReady)
    }

    private suspend fun CameraViewModel.nextEffectOrNull(): CameraEffect? =
        withTimeoutOrNull(100) { effects.first() }

    @Test
    fun captureClicked_whenNotReady_doesNothing() = runTest {
        val vm = viewModel()

        vm.onIntent(CameraIntent.CaptureClicked)

        assertFalse(vm.state.value.isCapturing)
        assertNull(vm.nextEffectOrNull())
    }

    @Test
    fun captureClicked_whenReady_emitsOneCapturePhotoWithTheDailyShareTarget() = runTest {
        val vm = viewModel().also { it.makeReady() }

        vm.onIntent(CameraIntent.CaptureClicked)
        vm.onIntent(CameraIntent.CaptureClicked)

        val effect = vm.nextEffectOrNull() as CameraEffect.CapturePhoto
        assertEquals("IMG_20261021_003012_123.jpg", effect.target.displayName)
        assertEquals("Pictures/DailyShare/2026-10-21/", effect.target.relativePath)
        assertTrue(vm.state.value.isCapturing)
        assertNull(vm.nextEffectOrNull())
    }

    @Test
    fun captureSucceeded_countsThePhotoAndAllowsTheNextShot() = runTest {
        val vm = viewModel().also { it.makeReady() }
        vm.onIntent(CameraIntent.CaptureClicked)

        vm.onIntent(CameraIntent.CaptureSucceeded)

        assertEquals(1, vm.state.value.savedCount)
        assertTrue(vm.state.value.canCapture)
    }

    @Test
    fun captureFailed_emitsAToastEffectAndAllowsARetry() = runTest {
        val vm = viewModel().also { it.makeReady() }
        vm.onIntent(CameraIntent.CaptureClicked)
        vm.nextEffectOrNull() // CapturePhoto

        vm.onIntent(CameraIntent.CaptureFailed)

        assertEquals(CameraEffect.ShowCaptureFailed, vm.nextEffectOrNull())
        assertEquals(0, vm.state.value.savedCount)
        assertTrue(vm.state.value.canCapture)
    }

    @Test
    fun denyingAfterARequest_withoutRationale_needsSettings() = runTest {
        val vm = viewModel()

        vm.onIntent(CameraIntent.PermissionObserved(granted = false, shouldShowRationale = false))
        assertEquals(CameraPermissionState.Requestable, vm.state.value.permissionState)

        vm.onIntent(CameraIntent.RequestPermissionClicked)
        assertEquals(CameraEffect.RequestPermission, vm.nextEffectOrNull())

        vm.onIntent(CameraIntent.PermissionObserved(granted = false, shouldShowRationale = false))
        assertEquals(CameraPermissionState.NeedsSettings, vm.state.value.permissionState)
    }

    @Test
    fun openSettingsClicked_emitsOpenSettings() = runTest {
        val vm = viewModel()

        vm.onIntent(CameraIntent.OpenSettingsClicked)

        assertEquals(CameraEffect.OpenSettings, vm.nextEffectOrNull())
    }
}