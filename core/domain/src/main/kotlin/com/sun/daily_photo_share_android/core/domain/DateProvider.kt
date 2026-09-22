package com.sun.daily_photo_share_android.core.domain

import java.time.LocalDate

/** Source of the current calendar date. Abstracted so date-dependent logic can be tested across midnight. */
fun interface DateProvider {
    fun today(): LocalDate
}

/** Reads the JVM default time zone on every call. */
class SystemDateProvider : DateProvider {
    override fun today(): LocalDate = LocalDate.now()
}