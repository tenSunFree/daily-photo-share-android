package com.sun.daily_photo_share_android.feature.today

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeOnly: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateAndTime: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d HH:mm")

/** "HH:mm" for a share made today; "M/d HH:mm" for an earlier day, so it never reads as today. */
internal fun formatShareTime(
    instant: Instant,
    today: LocalDate,
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val zoned = instant.atZone(zone)
    return if (zoned.toLocalDate() == today) timeOnly.format(zoned) else dateAndTime.format(zoned)
}