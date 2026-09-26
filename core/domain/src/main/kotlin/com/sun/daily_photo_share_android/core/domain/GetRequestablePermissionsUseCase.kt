package com.sun.daily_photo_share_android.core.domain

class GetRequestablePermissionsUseCase(
    private val repository: PhotoPermissionRepository,
) {
    operator fun invoke(): List<String> = repository.permissionsToRequest()
}