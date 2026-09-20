package com.sun.daily_photo_share_android.core.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * "Path and filename rules" for DailyShare photos. Pure naming logic; does not interact with MediaStore.
 * The actual write operation (using ContentResolver / RELATIVE_PATH) is handled in `:core:media`.
 */
object DailyShareMediaNaming {

    const val ROOT_PATH = "Pictures/DailyShare"
    private const val ROOT_PREFIX = "$ROOT_PATH/"

    private val folderFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd
    private val fileFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")

    /** MediaStore RELATIVE_PATH, ending with "/" */
    fun relativePathFor(date: LocalDate): String = "$ROOT_PREFIX${date.format(folderFormatter)}/"

    /** Generate a filename using the local time when the shutter was pressed; milliseconds prevent name collisions during burst shots. */
    fun fileNameFor(capturedAt: LocalDateTime): String =
        "IMG_${capturedAt.format(fileFormatter)}.jpg"

    /** Compare with "/" to avoid misjudging "Pictures/DailyShareOther/". */
    fun isDailySharePath(relativePath: String?): Boolean =
        relativePath?.startsWith(ROOT_PREFIX) == true

    /** Parse the date folder from RELATIVE_PATH; return null if not DailyShare or not a date folder. */
    fun dateFromRelativePath(relativePath: String?): LocalDate? {
        val path = relativePath?.takeIf { isDailySharePath(it) } ?: return null
        val folder = path.removePrefix(ROOT_PREFIX).substringBefore('/')
        return try {
            LocalDate.parse(folder, folderFormatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}