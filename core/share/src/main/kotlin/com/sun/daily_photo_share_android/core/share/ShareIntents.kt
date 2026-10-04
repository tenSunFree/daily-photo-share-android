package com.sun.daily_photo_share_android.core.share

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import com.sun.daily_photo_share_android.core.model.ShareRequest
import com.sun.daily_photo_share_android.core.model.ShareTarget

/**
 * Builds the Intent that hands a [ShareRequest] to another app. Launching it is the caller's job,
 * because only an Activity should start another app's screen.
 */
object ShareIntents {

    const val LINE_PACKAGE = "jp.naver.line.android"

    /**
     * One photo uses ACTION_SEND, several use ACTION_SEND_MULTIPLE in share order.
     * LINE targets the LINE package directly: starting it throws ActivityNotFoundException when LINE
     * is missing or disabled, so no package-visibility <queries> declaration is needed.
     */
    fun create(request: ShareRequest, chooserTitle: CharSequence?): Intent {
        val uris = request.uris.map { Uri.parse(it.value) }
        val send = if (uris.size == 1) {
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uris.single())
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE)
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        }.apply {
            type = "image/*"
            // The read grant travels with ClipData, so the receiving app needs no photo permission.
            clipData = clipDataOf(uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return when (request.target) {
            ShareTarget.LINE -> send.setPackage(LINE_PACKAGE)
            ShareTarget.SYSTEM_SHEET -> Intent.createChooser(send, chooserTitle)
        }
    }

    private fun clipDataOf(uris: List<Uri>): ClipData =
        ClipData.newRawUri(null, uris.first()).apply {
            uris.drop(1).forEach { addItem(ClipData.Item(it)) }
        }
}