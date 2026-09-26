package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

class GetPhotoPermissionStateUseCase(
    private val repository: PhotoPermissionRepository,
) {
    operator fun invoke(): PhotoPermissionState = repository.currentState()
}