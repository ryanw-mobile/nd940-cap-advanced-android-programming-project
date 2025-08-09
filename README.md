## Political Preparedness<br />![Gradle Build](https://github.com/ryanw-mobile/nd940-cap-advanced-android-programming-project/actions/workflows/main_build.yml/badge.svg) [![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://renovatebot.com/) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/24016f4f5fb146dea9e61d929ec13df6)](https://app.codacy.com/gh/ryanw-mobile/nd940-cap-advanced-android-programming-project/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/24016f4f5fb146dea9e61d929ec13df6)](https://app.codacy.com/gh/ryanw-mobile/nd940-cap-advanced-android-programming-project/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)

Political Preparedness is a Capstone project I have submitted for graduating from the Udacity
Android Developers Kotlin Nanodegree program in September 2021. As already being assessed in
previous coursework, Udacity did not award extra marks for writing tests in this project.

So I have followed a practical approach - by submitting what they have asked for in the rubric in
order to graduate. After graduation, now I keep on refactoring the codes, improving the UI layout
and adding tests to this project.

![Screenshot1](screenshots/screen0.png) ![Screenshot2](screenshots/screen1.png)
![Screenshot3](screenshots/screen2.png) ![Screenshot4](screenshots/screen3.png)
![Screenshot5](screenshots/screen4.png) ![Screenshot6](screenshots/screen5.png)
![Screenshot7](screenshots/screen6.png)


## Binaries download

If you want to try out the app without building it, check out
the [Releases section](https://github.com/ryanw-mobile/nd940-cap-advanced-android-programming-project/releases) where you can find
the APK and App Bundles for each major version. A working Civic API key was applied when building
the app, therefore you can test it by just installing it.

&nbsp;

## To-do lists

Planned enhancements are
now [logged as issues](https://github.com/ryanw-mobile/nd940-cap-advanced-android-programming-project/issues?q=is%3Aopen+is%3Aissue+label%3Arefactor%2Cfeature%2Cfix%2Ctest).

&nbsp;

## High level architecture

* Kotlin
* Kotlin Coroutines and Flow
* MVVM & clean architecture
* XML Views
* Device permission
* Device location
* Dark theme support
* Dependency Injection using Dagger Hilt
* Gradle Kotlin DSL and Version Catalog
* Robolectric database tests

&nbsp;

## Major libraries used

### Dependencies

* [AndroidX Activity KTX](https://developer.android.com/jetpack/androidx/releases/activity) - Apache 2.0 - Extensions for Android Activity
* [AndroidX Core KTX](https://developer.android.com/jetpack/androidx/releases/core) - Apache 2.0 - Extensions for Android Core
* [AndroidX Core Testing](https://developer.android.com/jetpack/androidx/releases/arch-core) - Apache 2.0 - Core testing utilities
* [AndroidX Espresso](https://developer.android.com/training/testing/espresso) - Apache 2.0 - UI testing framework for Android
* [AndroidX Fragment KTX](https://developer.android.com/jetpack/androidx/releases/fragment) - Apache 2.0 - Extensions for Android Fragment
* [AndroidX Fragment Testing](https://developer.android.com/jetpack/androidx/releases/fragment) - Apache 2.0 - Utilities for testing fragments
* [AndroidX Navigation KTX](https://developer.android.com/jetpack/androidx/releases/navigation) - Apache 2.0 - KTX extensions for Navigation components
* [AndroidX Room](https://developer.android.com/jetpack/androidx/releases/room) - Apache 2.0 - Database access with annotation processing and Kotlin extensions
* [Jetpack Compose UI](https://developer.android.com/jetpack/compose) - Apache 2.0 - Modern declarative UI toolkit with testing and tooling support
* [AndroidX Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3) - Apache 2.0 - Material Design 3 components for Compose
* [ConstraintLayout](https://developer.android.com/jetpack/androidx/releases/constraintlayout) - Apache 2.0 - Constraint-based layout system
* [AndroidX UIAutomator](https://developer.android.com/training/testing/other-components/ui-automator) - Apache 2.0 - UI automation testing library for Android
* [Dexmaker Mockito](https://github.com/linkedin/dexmaker) - Apache 2.0 - DexMaker-based Mockito implementation for Android
* [Glide](https://github.com/bumptech/glide) - BSD - Image loading and caching library for Android
* [Hilt for Android](https://dagger.dev/hilt) - Apache 2.0 - Dependency injection with compile-time DI and testing support
* [JUnit + AndroidX Extensions](https://developer.android.com/training/testing/junit-rules) - Apache 2.0 / EPL - JUnit 4 with AndroidX KTX support for instrumented tests
* [Kotlin Standard Library](https://kotlinlang.org/api/latest/jvm/stdlib/) - Apache 2.0 - Core language features for Kotlin
* [Kotlin Reflect](https://kotlinlang.org/docs/reflection.html) - Apache 2.0 - Reflection support for Kotlin
* [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Apache 2.0 - Support for asynchronous and concurrent programming in Kotlin
* [AndroidX Lifecycle LiveData KTX](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Apache 2.0 - Kotlin extensions for LiveData
* [AndroidX Lifecycle ViewModel KTX](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Apache 2.0 - Kotlin extensions for ViewModel
* [Material Components](https://material.io/develop/android) - Apache 2.0 - Material Design UI components for Android
* [Mockito Core](https://site.mockito.org/) - MIT - Java mocking framework for unit tests
* [Moshi](https://github.com/square/moshi) - Apache 2.0 - Modern JSON library for Android and Java with Kotlin and adapter support
* [Play Services Location](https://developers.google.com/android/guides/overview) - Apache 2.0 - Location services from Google Play
* [Retrofit](https://square.github.io/retrofit/) - Apache 2.0 - Type-safe HTTP client for Android and Java
* [Retrofit Kotlin Coroutines Adapter](https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter) - Apache 2.0 - Coroutine support for Retrofit
* [Robolectric](http://robolectric.org/) - Apache 2.0 - JVM-based unit testing framework for Android
* [Timber](https://github.com/JakeWharton/timber) - Apache 2.0 - Lightweight logging library for Android

### Plugins

* [Android Application Plugin](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration) - Google - Plugin for building Android applications
* [Jetbrains Kotlin Android Plugin](https://kotlinlang.org/docs/android.html) - JetBrains - Plugin for Kotlin Android projects
* [Compose Compiler Plugin](https://developer.android.com/jetpack/compose/setup) - JetBrains - Compose plugin for Kotlin
* [Hilt Android Plugin](https://dagger.dev/hilt) - Google - Plugin for Hilt Android dependency injection
* [Kover Plugin](https://github.com/Kotlin/kotlinx-kover) - JetBrains - Code coverage tool for Kotlin
* [KSP Plugin](https://github.com/google/ksp) - Google - Kotlin Symbol Processing API
* [Android Test Plugin](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration) - Google - Plugin for Android test projects
* [Serialization Plugin](https://github.com/Kotlin/kotlinx.serialization) - JetBrains - Plugin for Kotlin serialization
* [Navigation Safe Args Plugin](https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args) - AndroidX - Plugin for type-safe navigation
* [Detekt Plugin](https://github.com/detekt/detekt) - Artur Bosch - Static code analysis for Kotlin
* [Kotlinter Plugin](https://github.com/jeremymailen/kotlinter-gradle) - Jeremy Mailen - Plugin for Kotlin linting

&nbsp;

## Building the App
### Requirements

* To build the app by yourself, you need your own [Civic API key from Google](https://console.developers.google.com/)
* You can then either provide the API key in a `keystore.properties` file (`civicApiKey`), or set it as an environment variable `CIVIC_API_KEY`

### Without Keystore (Debug Builds)

By default, debug builds do not require a keystore. You can run:

```bash
./gradlew assembleDebug
```

No signing config is required unless you explicitly build a release variant.

### With Keystore (Release Builds)

Signing configuration is only triggered when:
- the task includes "Release" or "Bundle"
- or the environment variable `CI=true` is set

There are two ways to supply the keystore:

#### 1. Environment Variables (For CI)

Provide the following environment variables (e.g. in GitHub Secrets):

```
KEYSTORE_LOCATION=./keystore.jks
CI_ANDROID_KEYSTORE_ALIAS=yourAlias
CI_ANDROID_KEYSTORE_PASSWORD=yourKeystorePassword
CI_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD=yourPrivateKeyPassword
CIVIC_API_KEY=yourCivicApiKey
```

#### 2. `keystore.properties` File (For Local Builds)

Create a `keystore.properties` file at the root:

```properties
alias=yourAlias
pass=yourPrivateKeyPassword
store=path/to/keystore.jks
storePass=yourKeystorePassword
civicApiKey=yourCivicApiKey
```

Then build:

```bash
./gradlew bundleRelease
```

### Output Format

Release builds are timestamped using the format:

```
<app-name>-<buildType>-<versionName>-<yyyyMMdd-HHmmss>.apk
```

This applies to both APK and AAB artifacts.
