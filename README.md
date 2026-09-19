# daily-photo-share-android

[![Status](https://img.shields.io/badge/Status-In%20Progress-orange)](#roadmap)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/UI-Material%203-6750A4?logo=materialdesign&logoColor=white)](https://m3.material.io)
[![AGP](https://img.shields.io/badge/AGP-9.2.1-3DDC84?logo=android&logoColor=white)](https://developer.android.com/build)
[![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)](#environment)
[![targetSdk](https://img.shields.io/badge/targetSdk-37-3DDC84?logo=android&logoColor=white)](#environment)

---

## Introduction

Production-style Android photo workflow app with CameraX capture, automatic date-based MediaStore organization, full device gallery browsing, cross-source multi-selection, and LINE sharing.

The target stack is Kotlin, Jetpack Compose, MVI / UDF, Clean Architecture, Paging 3, Jetpack Glance widgets, multi-module, automated testing, CI/CD, and observability.

This project is for learning and technical practice.

> **Project status:** the project is in its early stage. The Gradle / Compose foundation is in place, and the feature set below is being built incrementally. See [Roadmap](#roadmap) for what is done and what is planned.

---

## Preview

Screenshots will be added once the first end-to-end flow (capture → organize → share) is available.

---

## Roadmap

### Implemented

- [x] Single-module Android project (`:app`) with Kotlin and Jetpack Compose
- [x] Material 3 theme with dynamic color on Android 12+ and light / dark fallback schemes
- [x] Edge-to-edge single-activity setup
- [x] Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`)
- [x] Gradle configuration cache enabled
- [x] Gradle daemon JVM toolchain pinned to JDK 21 via `gradle/gradle-daemon-jvm.properties`
- [x] Local unit test and instrumented test scaffolding (JUnit 4, AndroidX Test, Espresso, Compose UI test)
- [x] Android-focused `.gitignore` (Gradle, Kotlin, IDE, keystores, local config)

### Planned

- [ ] Camera capture with CameraX
- [ ] Automatic date-based organization through MediaStore
- [ ] Full device gallery browsing with Paging 3
- [ ] Cross-source multi-selection
- [ ] LINE sharing
- [ ] Jetpack Glance home screen widgets
- [ ] MVI / UDF presentation layer and Clean Architecture layering
- [ ] Multi-module split
- [ ] Automated testing (unit, Compose UI, instrumented)
- [ ] CI/CD with GitHub Actions
- [ ] Observability (crash reporting and analytics)

---

## Features

> Features in this section describe the intended scope. Items are marked as **Planned** until they are implemented.

### Camera Capture (Planned)

- Capture photos in-app with CameraX
- Save captured photos directly into MediaStore
- Runtime camera permission handling with clear denied / permanently denied states

### Date-Based Organization (Planned)

- Automatically group saved photos by date through MediaStore
- Keep files visible to the system gallery and other apps without extra copies

### Gallery Browsing (Planned)

- Browse the full device gallery, not only photos captured by this app
- Paginated loading with Paging 3 for large libraries
- Consistent loading, empty, and error states

### Multi-Selection and Sharing (Planned)

- Select multiple photos across different sources in a single session
- Share the selection to LINE through the Android share flow

### Home Screen Widgets (Planned)

- Jetpack Glance widgets for quick access to the daily photo workflow

### Architecture (Planned)

- Clean Architecture with separation of data, domain, and presentation layers
- MVI / unidirectional data flow with immutable UI state
- Multi-module project structure

### Testing (Planned)

- Unit tests for domain logic and repositories
- Compose UI tests for key screens
- Instrumented tests for MediaStore and camera-related flows

### Git Workflow and CI/CD (Planned)

- Feature branch workflow with protected `main` branch
- GitHub Actions for build, unit tests, and lint on every Pull Request
- Release signing with keystore and credentials restored from GitHub Secrets, keeping sensitive files out of the repository

### Observability (Planned)

- Crash reporting and performance monitoring
- Analytics for key user interactions

---

## Tech Stack

### In use

- Kotlin  
  Primary language (Kotlin `2.2.10`, official code style)
- Jetpack Compose + Material 3  
  Declarative UI toolkit (Compose BOM `2026.02.01`, dynamic color on Android 12+, edge-to-edge layout)
- AndroidX Activity Compose / Lifecycle Runtime KTX / Core KTX  
  Activity integration, lifecycle-aware components, and Kotlin extensions
- Android Gradle Plugin + Gradle Kotlin DSL  
  Build system (AGP `9.2.1`, Gradle `9.4.1`, version catalog, configuration cache)
- JUnit 4 / AndroidX Test / Espresso / Compose UI Test  
  Testing dependencies wired into the project for local and instrumented tests

### Planned

- CameraX  
  Camera preview and photo capture
- MediaStore  
  Saving and organizing photos by date, and reading the full device gallery
- Paging 3  
  Paginated gallery loading
- Jetpack Glance  
  Home screen widgets
- GitHub Actions  
  Build, test, and release automation

---

## Environment

- compileSdk / targetSdk: `37`
- minSdk: `24`
- Kotlin: `2.2.10`
- Android Gradle Plugin: `9.2.1`
- Gradle: `9.4.1` (via the Gradle wrapper)
- Gradle daemon JDK: `21` (resolved automatically by the Foojay toolchain resolver)
- Java source / target compatibility: `11`

---

## Local Development

### Open in Android Studio

1. Clone the repository
2. Open the project root in Android Studio
3. Wait for Gradle sync to finish (the wrapper downloads Gradle and the JDK 21 toolchain on first run)
4. Select the `app` run configuration and run it on an emulator or a physical device

### Command line

```bash
./gradlew assembleDebug             # build a debug APK
./gradlew test                      # run local unit tests
./gradlew connectedAndroidTest      # run instrumented tests (device or emulator required)
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Local configuration and secrets

The following files are intentionally excluded from version control by `.gitignore`:

- `local.properties` — local SDK path
- `keystore.properties`, `*.jks`, `*.keystore` — release signing material
- `google-services.json` — Firebase configuration (restore it locally or from CI secrets when Firebase is added)
- `.env` — local environment values
- `.idea/`, `.gradle/`, `.kotlin/`, `build/` — IDE and build outputs

The Gradle wrapper JAR (`gradle/wrapper/gradle-wrapper.jar`) is kept in the repository on purpose.

---

## Project Structure

> This is the current single-module layout. It will evolve into a multi-module structure as features are added.

```text
daily-photo-share-android
├─ app
│  └─ src
│     ├─ main
│     │  ├─ java/com/sun/daily_photo_share_android
│     │  │  ├─ MainActivity.kt
│     │  │  └─ ui
│     │  │     └─ theme            # Color.kt, Theme.kt, Type.kt
│     │  ├─ keepRules              # R8 keep rules
│     │  ├─ res                    # Icons, colors, strings, themes, backup rules
│     │  └─ AndroidManifest.xml
│     ├─ test                      # Local unit tests
│     └─ androidTest               # Instrumented tests
├─ gradle
│  ├─ libs.versions.toml           # Version catalog
│  ├─ gradle-daemon-jvm.properties # Daemon JDK toolchain (JDK 21)
│  └─ wrapper
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle.properties
├─ gradlew
├─ gradlew.bat
└─ README.md
```

---

## Notes

This project is created for independent learning and demonstration purposes.

Badges for CI, coverage, and monitoring will be added to the top of this README once the corresponding pipelines are set up.

---

## License

This repository is intended for learning and demonstration.

If you plan to open-source it, please choose a license and confirm third-party asset usage rights.