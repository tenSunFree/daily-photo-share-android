package com.sun.daily_photo_share_android.core.model

/**
 * Media identification key = content URI string.
 *
 * We do not use the MediaStore `_ID` (Long); temporary URIs returned by the Photo Picker lack a MediaStore ID,
 * yet they still need to be included in the selection. Conversion to `android.net.Uri` occurs only at the sharing boundary (:core:media / share).
 */
@JvmInline
value class MediaUri(val value: String) {
    override fun toString(): String = value
}