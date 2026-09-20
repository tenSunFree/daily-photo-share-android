plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Pure JVM module: The Gradle daemon can run on JDK 21, but the compilation target is explicitly set to 17 here.
// (The foojay resolver is configured in settings.gradle.kts; JDK 17 will be downloaded automatically if not present locally.)
kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.junit)
}