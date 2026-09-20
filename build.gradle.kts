// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    // Android modules in AGP 9 include Kotlin by default; do not add org.jetbrains.kotlin.android.
    // Pure JVM modules (e.g., :core:model) still require kotlin-jvm.
    alias(libs.plugins.kotlin.jvm) apply false
    // The Compose compiler plugin is independent of the built-in Kotlin support and still requires declaration.
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}