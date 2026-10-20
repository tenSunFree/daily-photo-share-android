package com.sun.daily_photo_share_android.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareRecordDaoTest {

    private lateinit var database: DailyDatabase
    private lateinit var dao: ShareRecordDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DailyDatabase::class.java).build()
        dao = database.shareRecordDao()
    }

    @After
    fun tearDown() = database.close()

    private fun entity(at: Long, count: Int = 1, target: String = "LINE") = ShareRecordEntity(
        handedOffAtEpochMillis = at,
        photoCount = count,
        target = target,
        status = "HANDED_OFF",
    )

    @Test
    fun latest_isNull_whenNothingWasShared() = runTest {
        assertNull(dao.observeLatest().first())
    }

    @Test
    fun latest_isTheNewestByTime_notByInsertOrder() = runTest {
        dao.insert(entity(at = 2_000, count = 3))
        dao.insert(entity(at = 1_000, count = 1))

        assertEquals(3, dao.observeLatest().first()?.photoCount)
    }

    @Test
    fun latest_usesTheLaterInsert_whenTimesAreEqual() = runTest {
        dao.insert(entity(at = 1_000, count = 1))
        dao.insert(entity(at = 1_000, count = 2))

        assertEquals(2, dao.observeLatest().first()?.photoCount)
    }
}