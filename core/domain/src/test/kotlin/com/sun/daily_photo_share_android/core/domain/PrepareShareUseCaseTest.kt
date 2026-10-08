package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrepareShareUseCaseTest {

    private val a = MediaUri("content://media/external/images/media/1")
    private val b = MediaUri("content://media/external/images/media/2")
    private val c = MediaUri("content://media/external/images/media/3")

    private class FakeChecker(
        private val readable: Set<MediaUri>,
        private val reversed: Boolean = false,
    ) : MediaReadabilityChecker {
        var calls = 0
        override suspend fun readable(uris: List<MediaUri>): List<MediaUri> {
            calls++
            val result = uris.filter { it in readable }
            return if (reversed) result.reversed() else result
        }
    }

    @Test
    fun emptySelection_isNothingSelected_withoutCheckingAnything() = runTest {
        val checker = FakeChecker(setOf(a))

        val result = PrepareShareUseCase(checker)(emptyList(), ShareTarget.LINE)

        assertEquals(SharePreparation.NothingSelected, result)
        assertEquals(0, checker.calls)
    }

    @Test
    fun allReadable_keepsShareOrderAndTarget() = runTest {
        val result =
            PrepareShareUseCase(FakeChecker(setOf(a, b, c)))(listOf(c, a, b), ShareTarget.LINE)

        assertEquals(
            SharePreparation.Ready(ShareRequest(listOf(c, a, b), ShareTarget.LINE), emptyList()),
            result,
        )
    }

    @Test
    fun unreadablePhotos_areSkipped_andReported() = runTest {
        val result =
            PrepareShareUseCase(FakeChecker(setOf(a, c)))(listOf(a, b, c), ShareTarget.SYSTEM_SHEET)

        assertEquals(
            SharePreparation.Ready(ShareRequest(listOf(a, c), ShareTarget.SYSTEM_SHEET), listOf(b)),
            result,
        )
    }

    @Test
    fun shareOrder_followsTheSelection_evenIfTheCheckerReordersResults() = runTest {
        val result = PrepareShareUseCase(FakeChecker(setOf(a, b, c), reversed = true))(
            listOf(b, c, a),
            ShareTarget.LINE,
        )

        assertTrue(result is SharePreparation.Ready)
        assertEquals(listOf(b, c, a), (result as SharePreparation.Ready).request.uris)
    }

    @Test
    fun nothingReadable_reportsEveryPhoto() = runTest {
        val result = PrepareShareUseCase(FakeChecker(emptySet()))(listOf(a, b), ShareTarget.LINE)

        assertEquals(SharePreparation.NothingReadable(listOf(a, b)), result)
    }
}