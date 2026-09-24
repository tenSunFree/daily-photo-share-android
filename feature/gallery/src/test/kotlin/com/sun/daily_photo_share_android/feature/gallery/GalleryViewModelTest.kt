package com.sun.daily_photo_share_android.feature.gallery

import androidx.paging.PagingData
import com.sun.daily_photo_share_android.core.domain.GetPhotoPermissionStateUseCase
import com.sun.daily_photo_share_android.core.domain.GetRequestablePermissionsUseCase
import com.sun.daily_photo_share_android.core.domain.MediaGalleryRepository
import com.sun.daily_photo_share_android.core.domain.ObserveDevicePhotosUseCase
import com.sun.daily_photo_share_android.core.domain.PhotoPermissionRepository
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import com.sun.daily_photo_share_android.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestable = listOf("android.permission.READ_MEDIA_IMAGES")

    private class FakePermissionRepository(
        var state: PhotoPermissionState,
        private val permissions: List<String>,
    ) : PhotoPermissionRepository {
        override fun currentState(): PhotoPermissionState = state
        override fun permissionsToRequest(): List<String> = permissions
    }

    private object EmptyGalleryRepository : MediaGalleryRepository {
        override fun observePhotos(): Flow<PagingData<MediaPhoto>> = flowOf(PagingData.empty())
    }

    private fun viewModel(permissions: FakePermissionRepository) = GalleryViewModel(
        getPermissionState = GetPhotoPermissionStateUseCase(permissions),
        getRequestablePermissions = GetRequestablePermissionsUseCase(permissions),
        observeDevicePhotos = ObserveDevicePhotosUseCase(EmptyGalleryRepository),
    )

    @Test
    fun initialState_isUnchecked_untilTheFirstCheck() {
        val vm = viewModel(FakePermissionRepository(PhotoPermissionState.Full, requestable))

        assertNull(vm.state.value.permissionState)
    }

    @Test
    fun checkPermission_readsTheCurrentState() {
        val vm = viewModel(FakePermissionRepository(PhotoPermissionState.Partial, requestable))

        vm.onIntent(GalleryIntent.CheckPermission)

        assertEquals(PhotoPermissionState.Partial, vm.state.value.permissionState)
    }

    @Test
    fun checkPermission_picksUpAChangeMadeInSettings() {
        val permissions = FakePermissionRepository(PhotoPermissionState.Full, requestable)
        val vm = viewModel(permissions)

        vm.onIntent(GalleryIntent.CheckPermission)
        permissions.state = PhotoPermissionState.Denied
        vm.onIntent(GalleryIntent.CheckPermission)

        assertEquals(PhotoPermissionState.Denied, vm.state.value.permissionState)
    }

    @Test
    fun requestPermissionClicked_emitsTheRequestablePermissions() = runTest {
        val vm = viewModel(FakePermissionRepository(PhotoPermissionState.Denied, requestable))

        vm.onIntent(GalleryIntent.RequestPermissionClicked)

        assertEquals(GalleryEffect.RequestPermissions(requestable), vm.effects.first())
    }

    @Test
    fun manageSelectedPhotosClicked_requestsTheSamePermissionsAgain() = runTest {
        val vm = viewModel(FakePermissionRepository(PhotoPermissionState.Partial, requestable))

        vm.onIntent(GalleryIntent.ManageSelectedPhotosClicked)

        assertEquals(GalleryEffect.RequestPermissions(requestable), vm.effects.first())
    }

    @Test
    fun openSettingsClicked_emitsOpenAppSettings() = runTest {
        val vm = viewModel(FakePermissionRepository(PhotoPermissionState.Denied, requestable))

        vm.onIntent(GalleryIntent.OpenSettingsClicked)

        assertEquals(GalleryEffect.OpenAppSettings, vm.effects.first())
    }
}