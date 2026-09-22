# daily-photo-share-android

[![Status](https://img.shields.io/badge/Status-Under%20Development-orange)](#roadmap)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/UI-Material%203-6750A4?logo=materialdesign&logoColor=white)](https://m3.material.io)
[![AGP](https://img.shields.io/badge/AGP-9.2.1-3DDC84?logo=android&logoColor=white)](https://developer.android.com/build)
[![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![minSdk](https://img.shields.io/badge/minSdk-29-3DDC84?logo=android&logoColor=white)](#environment)
[![targetSdk](https://img.shields.io/badge/targetSdk-37-3DDC84?logo=android&logoColor=white)](#environment)

---

## Introduction

Production-style Android photo workflow app with CameraX capture, automatic date-based MediaStore organization, full device gallery browsing, cross-source multi-selection, and LINE sharing.

The target stack is Kotlin, Jetpack Compose, MVI / UDF, Clean Architecture, Paging 3, Jetpack Glance widgets, multi-module, automated testing, CI/CD, and observability.

This project is for learning and technical practice.

> **Project status:** under development. See [Roadmap](#roadmap) for what is implemented and what is planned.

---

## Concepts

- **Today screen** — the app's daily workspace: it shows the photos captured *by this app* on the calendar date of the device, the time of the last capture, and the most recent share. "Today" is a screen name, not a reference to the project timeline.
- **DailyShare album** — photos captured by this app, stored under `Pictures/DailyShare/<yyyy-MM-dd>/`.
- **Device gallery** — every image on the device that the app has permission to read (camera, screenshots, downloads, chat apps, and so on), including but not limited to the DailyShare album.

---

## Preview

Screenshots are added once the capture → organize → share flow is implemented.

---

## Roadmap

### Implemented

- [x] Multi-module foundation: `:app`, `:core:model` (pure Kotlin/JVM), `:core:designsystem` (Android library)
- [x] Design System foundation: spacing / size tokens, `DailyTheme` (Material 3, dynamic color on Android 12+), reusable `DailyButton`
- [x] Domain foundation: DailyShare path and filename rules (cross-midnight safe), ordered `PhotoSelection`, core domain models, covered by JVM unit tests
- [x] Hilt dependency injection bootstrap with KSP
- [x] Edge-to-edge single-activity setup
- [x] Gradle Kotlin DSL with a version catalog, configuration cache, and build cache
- [x] Gradle daemon JVM toolchain pinned to JDK 21 via `gradle/gradle-daemon-jvm.properties`
- [x] Local unit test and instrumented test scaffolding (JUnit 4, AndroidX Test, Espresso, Compose UI test)
- [x] Android-focused `.gitignore` (Gradle, Kotlin, IDE, keystores, local config)
- [x] Navigation shell: single activity, type-safe routes, bottom navigation (Today | Camera | Gallery)
- [x] Today screen as an MVI / UDF vertical slice (Intent, immutable UiState, Effect, reducer, ViewModel) on sample data, covered by reducer, ViewModel and use case unit tests

### Planned

- [ ] Today screen backed by real MediaStore data
- [ ] Camera capture with CameraX
- [ ] Automatic date-based organization through MediaStore
- [ ] Full device gallery browsing with Paging 3
- [ ] Cross-source multi-selection
- [ ] LINE sharing
- [ ] Jetpack Glance home screen widgets
- [ ] Remaining modules (`:core:data`, `:core:media`, `:core:database`, further feature modules), added together with the features that need them
- [ ] Automated testing beyond the current scope (Compose UI, instrumented)
- [ ] CI/CD with GitHub Actions
- [ ] Observability (crash reporting and analytics)

---

## Features

> This section describes the intended scope. The [Roadmap](#roadmap) is the single source of truth for implementation status.

### Camera Capture

- Capture photos in-app with CameraX
- Save captured photos directly into MediaStore
- Runtime camera permission handling with clear denied / permanently denied states

### Date-Based Organization

- Photos are saved to `Pictures/DailyShare/<yyyy-MM-dd>/` through MediaStore, using the local date at the moment the shutter is pressed
- Files stay visible to the system gallery and other apps without extra copies
- Photos belong to the user: they stay on the device even if the app is uninstalled

### Gallery Browsing

- Browse the full device gallery, not only photos captured by this app
- Paginated loading with Paging 3 for large libraries
- Consistent loading, empty, and error states
- Photo permission states: full, partial (Android 14+), and denied, with the system Photo Picker as a fallback

### Multi-Selection and Sharing

- Select multiple photos across different sources in a single session, with a stable share order
- Share the selection to LINE through the Android share flow, with the system share sheet as a fallback
- Share results are recorded as "handed off", never as "delivered"

### Home Screen Widgets

- Jetpack Glance widgets for quick access to the daily photo workflow

### Architecture

- Multi-module project structure; modules are added together with the features that need them
- Clean Architecture with separation of data, domain, and presentation layers
- MVI / unidirectional data flow with immutable UI state

### Testing

- Unit tests for domain logic
- Unit tests for reducers, ViewModels, and repositories
- Compose UI tests for key screens
- Instrumented tests for MediaStore and camera-related flows

### Git Workflow and CI/CD

- Feature branch workflow with protected `main` branch
- GitHub Actions for build, unit tests, and lint on every Pull Request
- Release signing with keystore and credentials restored from GitHub Secrets, keeping sensitive files out of the repository

### Observability

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
- Hilt + KSP  
  Dependency injection (Hilt `2.60.1`, KSP `2.3.9`)
- Navigation Compose (type-safe routes) + kotlinx.serialization  
  Single-activity navigation (Navigation Compose `2.10.1`)
- Lifecycle ViewModel + `androidx.hilt` ViewModel integration  
  State holders with `StateFlow`, lifecycle-aware collection
- Kotlin Coroutines / Flow  
  Asynchronous data streams in the domain layer
- Android Gradle Plugin + Gradle Kotlin DSL  
  Build system (AGP `9.2.1`, Gradle `9.4.1`, version catalog, configuration cache, build cache)
- Multi-module Gradle build  
  AGP 9 built-in Kotlin for Android modules, `kotlin-jvm` for the pure JVM `:core:model` and `:core:domain`
- JUnit 4 / AndroidX Test / Espresso / Compose UI Test  
  Testing dependencies wired into the project for local and instrumented tests

### Planned

- CameraX  
  Camera preview and photo capture
- MediaStore  
  Saving and organizing photos by date, and reading the full device gallery
- Paging 3  
  Paginated gallery loading
- Room / DataStore  
  Share history and preferences
- Jetpack Glance  
  Home screen widgets
- GitHub Actions  
  Build, test, and release automation

---

## Environment

- compileSdk / targetSdk: `37`
- minSdk: `29` (Android 10+, see [Architecture Decisions](#architecture-decisions))
- Kotlin: `2.2.10`
- Android Gradle Plugin: `9.2.1`
- Gradle: `9.4.1` (via the Gradle wrapper)
- Gradle daemon JDK: `21` (resolved automatically by the Foojay toolchain resolver)
- Java source / target compatibility: `17`

---

## Local Development

### Open in Android Studio

1. Clone the repository
2. Open the project root in Android Studio
3. Wait for Gradle sync to finish (the wrapper downloads Gradle and the JDK toolchains on first run)
4. Select the `app` run configuration and run it on an emulator or a physical device

### Command line

```bash
./gradlew assembleDebug             # build a debug APK
./gradlew test                      # run all local unit tests
./gradlew :core:model:test          # run pure-JVM domain tests only (fast)
./gradlew connectedAndroidTest      # run instrumented tests (device or emulator required)
```

On Windows, use `gradlew.bat` instead of `./gradlew` (PowerShell: `./gradlew` also works).

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

> Modules are added together with the features that need them, so the tree grows with the feature set.

```text
daily-photo-share-android
├─ app                                   # Application, Activity, navigation shell, Hilt wiring
├─ core
│  ├─ model                              # Pure Kotlin/JVM: domain models and rules (no Android)
│  ├─ domain                             # Pure Kotlin/JVM: repository interfaces and use cases
│  └─ designsystem                       # Android library: tokens, theme, icons, components
├─ feature
│  └─ today                              # Today screen: MVI contract, reducer, ViewModel, UI
├─ gradle
│  ├─ libs.versions.toml                 # Version catalog
│  ├─ gradle-daemon-jvm.properties       # Daemon JDK toolchain (JDK 21)
│  └─ wrapper
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle.properties
└─ README.md
```

Module dependency direction:

```text
:app ──► :feature:today ──► :core:domain ──► :core:model
 │              └──────────► :core:designsystem
 └─► :core:domain, :core:model, :core:designsystem
```

`:core:model` and `:core:domain` stay free of Android and Compose; `:core:designsystem` does not depend on the domain modules.

---

## Architecture Decisions

- **minSdk 29 (Android 10+).** Photos are written to `Pictures/DailyShare/<date>/` through MediaStore `RELATIVE_PATH` (API 29). Supporting Android 7-9 would require a separate legacy storage path, which is not worth maintaining for this project.
- **Media identity is a content URI string (`MediaUri`).** Temporary URIs from the system Photo Picker have no MediaStore `_ID`, but must still be selectable and shareable.
- **`PhotoSelection` is an immutable ordered list.** List order is the share order and the on-screen badge number, so every operation is a pure function of the previous state (easy to reducer-test).
- **The domain model is Android-free.** `:core:model` and `:core:domain` use only `java.time`, Kotlin types and coroutines, so their tests run on the plain JVM in seconds. Conversion to `android.net.Uri` happens at the sharing boundary.
- **Modules are created on demand.** No empty placeholder modules; each module appears with its first real implementation.
- **No shared MVI framework up front.** Intent / State / Effect / Reducer are implemented directly in one feature; common abstractions are extracted only after several features show real duplication.
- **Retrofit / OkHttp are deferred.** The app is offline-first; a remote layer is added only when cloud features are.
- **Share status is `HANDED_OFF`, never "success".** The app can only prove it handed the images to another app, not that the message was sent or received.
- **`:core:domain` has no DI annotations.** Use cases are plain classes constructed in a Hilt module in `:app`, so the domain layer does not know which DI framework is used.
- **Screens are split into Route and Screen.** `XRoute` connects the ViewModel (collect state, forward intents, turn effects into navigation); `XScreen` is stateless (UiState in, Intent out).
- **Reload on every resume.** The Today screen re-reads its data whenever it resumes, so it also reflects a changed calendar date without a background timer.
- **Icons go through the design system.** Features use `DailyIcons` and never import an icon library directly, so the icon source can change in one module.

---

## Notes

This project is created for independent learning and demonstration purposes.

Badges for CI, coverage, and monitoring are added to the top of this README when the corresponding pipelines exist.

---

## License

This repository is intended for learning and demonstration.

If you plan to open-source it, please choose a license and confirm third-party asset usage rights.