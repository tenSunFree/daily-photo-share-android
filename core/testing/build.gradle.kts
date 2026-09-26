plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Exposed so test source sets get JUnit and coroutines-test from a single dependency.
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}