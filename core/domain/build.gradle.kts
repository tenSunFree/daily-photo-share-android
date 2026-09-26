plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Pure JVM module: the Gradle daemon can run on JDK 21, the compilation target is 17.
kotlin {
    jvmToolchain(17)
}

dependencies {
    // Repository / UseCase signatures expose model types, Flow and PagingData.
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
    // paging-common has no Android framework dependency: a deliberate, pragmatic dependency on Jetpack Paging.
    api(libs.androidx.paging.common)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}