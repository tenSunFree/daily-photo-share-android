plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.sun.daily_photo_share_android.feature.camera"
    compileSdk = 37
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    // ContextCompat and the Uri.toUri() extension.
    implementation(libs.androidx.core.ktx)
    // rememberLauncherForActivityResult lives here.
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // SavedStateHandle: remembers whether the camera permission was already requested.
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.compose)
    implementation(libs.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(project(":core:testing"))
}