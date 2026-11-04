package com.sun.daily_photo_share_android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@Module
@InstallIn(SingletonComponent::class)
internal object TimeModule {

    @Provides
    fun provideClock(): Clock = SystemZoneClock
}

/** A system clock whose zone follows the device's current time zone on every read. */
private object SystemZoneClock : Clock() {
    override fun getZone(): ZoneId = ZoneId.systemDefault()
    override fun withZone(zone: ZoneId): Clock = system(zone)
    override fun instant(): Instant = Instant.now()
}