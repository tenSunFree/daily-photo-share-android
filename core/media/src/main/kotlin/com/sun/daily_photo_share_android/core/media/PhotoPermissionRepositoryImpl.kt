package com.sun.daily_photo_share_android.core.media

import com.sun.daily_photo_share_android.core.domain.PhotoPermissionRepository
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import javax.inject.Inject

internal class PhotoPermissionRepositoryImpl @Inject constructor(
    private val checker: MediaPermissionChecker,
) : PhotoPermissionRepository {
    override fun currentState(): PhotoPermissionState = checker.currentState()
    override fun permissionsToRequest(): List<String> = checker.permissionsToRequest()
}