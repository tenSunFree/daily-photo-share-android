package com.sun.daily_photo_share_android.feature.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sun.daily_photo_share_android.core.domain.GetPhotoPermissionStateUseCase
import com.sun.daily_photo_share_android.core.domain.GetRequestablePermissionsUseCase
import com.sun.daily_photo_share_android.core.domain.MediaGalleryRepository
import com.sun.daily_photo_share_android.core.domain.MediaReadabilityChecker
import com.sun.daily_photo_share_android.core.domain.ObserveDevicePhotosUseCase
import com.sun.daily_photo_share_android.core.domain.PhotoPermissionRepository
import com.sun.daily_photo_share_android.core.domain.PrepareShareUseCase
import com.sun.daily_photo_share_android.core.model.MediaPhoto
import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget
import com.sun.daily_photo_share_android.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestable = listOf("android.permission.READ_MEDIA_IMAGES")
    private val a = MediaUri("content://media/external/images/media/1")
    private val b = MediaUri("content://media/external/images/media/2")
    private val c = MediaUri("content://media/external/images/media/3")

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

    /** null means every URI is readable. */
    private class FakeReadabilityChecker(var readable: Set<MediaUri>? = null) :
        MediaReadabilityChecker {
        override suspend fun readable(uris: List<MediaUri>): List<MediaUri> =
            readable?.let { allowed -> uris.filter { it in allowed } } ?: uris
    }

    private fun viewModel(
        permissions: FakePermissionRepository = FakePermissionRepository(
            PhotoPermissionState.Full,
            requestable
        ),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        checker: FakeReadabilityChecker = FakeReadabilityChecker(),
    ) = GalleryViewModel(
        getPermissionState = GetPhotoPermissionStateUseCase(permissions),
        getRequestablePermissions = GetRequestablePermissionsUseCase(permissions),
        observeDevicePhotos = ObserveDevicePhotosUseCase(EmptyGalleryRepository),
        prepareShare = PrepareShareUseCase(checker),
        savedStateHandle = savedStateHandle,
    )

    private fun GalleryViewModel.select(uris: List<MediaUri>) {
        onIntent(GalleryIntent.PhotoLongPressed(uris.first()))
        uris.drop(1).forEach { onIntent(GalleryIntent.PhotoTapped(it)) }
    }

    // --- permission ---

    @Test
    fun initialState_isUnchecked_untilTheFirstCheck() {
        val vm = viewModel()

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

    // --- selection persistence ---

    @Test
    fun selection_isSavedInOrder() {
        val handle = SavedStateHandle()
        val vm = viewModel(savedStateHandle = handle)

        vm.select(listOf(b, a))

        assertEquals(
            listOf(b.value, a.value),
            handle.get<List<String>>(GalleryViewModel.SELECTION_KEY)
        )
    }

    @Test
    fun selection_isRestoredAfterProcessDeath() {
        val handle =
            SavedStateHandle(mapOf(GalleryViewModel.SELECTION_KEY to arrayListOf(b.value, a.value)))

        val vm = viewModel(savedStateHandle = handle)

        assertEquals(listOf(b, a), vm.state.value.selection.items)
        assertTrue(vm.state.value.isSelectionMode)
    }

    @Test
    fun clearedSelection_isSavedAsEmpty() {
        val handle = SavedStateHandle()
        val vm = viewModel(savedStateHandle = handle)

        vm.select(listOf(a))
        vm.onIntent(GalleryIntent.ClearSelectionClicked)

        assertEquals(emptyList<String>(), handle.get<List<String>>(GalleryViewModel.SELECTION_KEY))
    }

    // --- sharing ---

    @Test
    fun shareToLine_launchesLineWithThePhotosInSelectionOrder() = runTest {
        val vm = viewModel()
        vm.select(listOf(c, a, b))

        vm.onIntent(GalleryIntent.ShareToLineClicked)

        assertEquals(
            GalleryEffect.LaunchShare(
                ShareRequest(listOf(c, a, b), ShareTarget.LINE),
                skippedCount = 0
            ),
            vm.effects.first(),
        )
        assertFalse(vm.state.value.isPreparingShare)
    }

    @Test
    fun share_launchesTheSystemShareSheet() = runTest {
        val vm = viewModel()
        vm.select(listOf(a))

        vm.onIntent(GalleryIntent.ShareClicked)

        assertEquals(
            GalleryEffect.LaunchShare(
                ShareRequest(listOf(a), ShareTarget.SYSTEM_SHEET),
                skippedCount = 0
            ),
            vm.effects.first(),
        )
    }

    @Test
    fun share_skipsUnreadablePhotos_andRemovesThemFromTheSelection() = runTest {
        val handle = SavedStateHandle()
        val vm = viewModel(savedStateHandle = handle, checker = FakeReadabilityChecker(setOf(a, c)))
        vm.select(listOf(a, b, c))

        vm.onIntent(GalleryIntent.ShareToLineClicked)

        assertEquals(
            GalleryEffect.LaunchShare(
                ShareRequest(listOf(a, c), ShareTarget.LINE),
                skippedCount = 1
            ),
            vm.effects.first(),
        )
        assertEquals(listOf(a, c), vm.state.value.selection.items)
        assertEquals(
            listOf(a.value, c.value),
            handle.get<List<String>>(GalleryViewModel.SELECTION_KEY)
        )
    }

    @Test
    fun share_withNothingReadable_showsTheProblem() {
        val vm = viewModel(checker = FakeReadabilityChecker(emptySet()))
        vm.select(listOf(a, b))

        vm.onIntent(GalleryIntent.ShareToLineClicked)

        assertEquals(ShareProblem.NothingReadable, vm.state.value.shareProblem)
        assertTrue(vm.state.value.selection.isEmpty)
    }

    @Test
    fun share_withoutASelection_doesNothing() {
        val vm = viewModel()

        vm.onIntent(GalleryIntent.ShareToLineClicked)

        assertFalse(vm.state.value.isPreparingShare)
        assertNull(vm.state.value.shareProblem)
    }

    @Test
    fun lineUnavailable_offersTheSystemShareSheet() = runTest {
        val vm = viewModel()
        val request = ShareRequest(listOf(a, b), ShareTarget.LINE)

        vm.onIntent(GalleryIntent.ShareTargetUnavailable(request))
        assertEquals(ShareProblem.LineNotInstalled(request), vm.state.value.shareProblem)

        vm.onIntent(GalleryIntent.UseSystemShareSheetClicked)

        assertNull(vm.state.value.shareProblem)
        assertEquals(
            GalleryEffect.LaunchShare(
                request.copy(target = ShareTarget.SYSTEM_SHEET),
                skippedCount = 0
            ),
            vm.effects.first(),
        )
    }

    @Test
    fun shareProblemDismissed_clearsTheProblem_andKeepsTheSelection() {
        val vm = viewModel()
        vm.select(listOf(a))
        vm.onIntent(GalleryIntent.ShareTargetUnavailable(ShareRequest(listOf(a), ShareTarget.LINE)))

        vm.onIntent(GalleryIntent.ShareProblemDismissed)

        assertNull(vm.state.value.shareProblem)
        assertEquals(listOf(a), vm.state.value.selection.items)
    }
}