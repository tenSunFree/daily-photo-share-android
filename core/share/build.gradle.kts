plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sun.daily_photo_share_android.core.share"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // ShareIntents takes a ShareRequest, so callers get the model types with it.
    api(project(":core:model"))
}