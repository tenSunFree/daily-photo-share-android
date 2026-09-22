plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Pure JVM module: the Gradle daemon can run on JDK 21, the compilation target is 17.
kotlin {
    jvmToolchain(17)
}

dependencies {
    // Repository / UseCase signatures expose model types and Flow.
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}