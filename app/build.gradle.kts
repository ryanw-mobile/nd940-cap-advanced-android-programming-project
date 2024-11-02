/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 */

import com.android.build.api.dsl.ManagedVirtualDevice
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltAndroidPlugin)
    alias(libs.plugins.kotlinxKover)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.gradleKtlint)
    alias(libs.plugins.serialization)
    alias(libs.plugins.navigationSafeArgs)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.android.politicalpreparedness"
    compileSdk = libs.versions.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            val isRunningOnCI = System.getenv("CI") == "true"
            val keystorePropertiesFile = file("../../keystore.properties")

            if (isRunningOnCI) {
                println("Signing Config: using environment variables")
                keyAlias = System.getenv("CI_ANDROID_KEYSTORE_ALIAS")
                keyPassword = System.getenv("CI_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
                storeFile = file(System.getenv("KEYSTORE_LOCATION"))
                storePassword = System.getenv("CI_ANDROID_KEYSTORE_PASSWORD")
            } else if (keystorePropertiesFile.exists()) {
                println("Signing Config: using keystore properties")
                val properties = Properties()
                InputStreamReader(
                    FileInputStream(keystorePropertiesFile),
                    Charsets.UTF_8,
                ).use { reader ->
                    properties.load(reader)
                }

                keyAlias = properties.getProperty("alias")
                keyPassword = properties.getProperty("pass")
                storeFile = file(properties.getProperty("store"))
                storePassword = properties.getProperty("storePass")
            } else {
                println("Signing Config: skipping signing")
            }
        }
    }

    defaultConfig {
        applicationId = "com.example.android.politicalpreparedness"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        buildToolsVersion = libs.versions.buildToolsVersion.get()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        resourceConfigurations += setOf("en")

        testInstrumentationRunner =
            "com.example.android.politicalpreparedness.ui.test.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Bundle output filename
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
        setProperty("archivesBaseName", "politicalpreparedness-$versionName-$timestamp")

        val isRunningOnCI = System.getenv("CI") == "true"
        val keystorePropertiesFile = file("../../keystore.properties")
        if (isRunningOnCI) {
            println("Importing Civic API Key from environment variable")
            defaultConfig.buildConfigField(
                type = "String",
                name = "CIVIC_API_KEY",
                value = System.getenv("CIVIC_API_KEY"),
            )
        } else if (keystorePropertiesFile.exists()) {
            println("Importing Civic API Key from keystore")
            val properties = Properties()
            InputStreamReader(
                FileInputStream(keystorePropertiesFile),
                Charsets.UTF_8,
            ).use { reader ->
                properties.load(reader)
            }

            defaultConfig.buildConfigField(
                type = "String",
                name = "CIVIC_API_KEY",
                value = properties.getProperty("civicApiKey") ?: "\"\"",
            )
        } else {
            println("Civic API key not found.")
            defaultConfig.buildConfigField(
                "String",
                "CIVIC_API_KEY",
                "\"\"",
            )
        }
    }

    buildTypes {
        fun setOutputFileName() {
            applicationVariants.all {
                val variant = this
                variant.outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
                        val outputFileName =
                            "politicalpreparedness-${variant.versionName}-$timestamp-${variant.name}.apk"
                        output.outputFileName = outputFileName
                    }
            }
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            setOutputFileName()
        }

        create("benchmark") {
            initWith(getByName("release"))
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                ),
            )

            signingConfigs.getByName("release").keyAlias?.let {
                signingConfig = signingConfigs.getByName("release")
            }

            setOutputFileName()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

//    packaging {
//        resources {
//            excludes +=
//                listOf(
//                    "META-INF/AL2.0",
//                    "META-INF/LGPL2.1",
//                    "META-INF/licenses/ASM",
//                    "META-INF/LICENSE.md",
//                    "META-INF/LICENSE*.md",
//                )
//            pickFirsts +=
//                listOf(
//                    "win32-x86-64/attach_hotspot_windows.dll",
//                    "win32-x86/attach_hotspot_windows.dll",
//                )
//        }
//    }

    sourceSets {
        named("test") {
            java.srcDirs("src/testFixtures/java")
        }
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        managedDevices {
            devices {
                create<ManagedVirtualDevice>("pixel2Api34") {
                    device = "Pixel 2"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }

    /**
     * Source sets can no longer contain shared roots as this is impossible to represent in the IDE.
     * In order to share sources between test and androidTest we should be able to use test fixtures.
     */
    testFixtures {
        enable = true
        androidResources = true
    }

//    sourceSets {
//        androidTest {
//            java.srcDirs += "src/sharedTest/java"
//        }
//        test {
//            java.srcDirs += "src/sharedTest/java"
//        }
//    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.reflect)
    // implementation "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$version_kotlin_serialization"

    // Constraint Layout
    implementation(libs.constraintlayout)

    // ViewModel and LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Core with Ktx
    implementation(libs.androidx.core.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    ksp("com.github.bumptech.glide:compiler:4.12.0")

    // Room
    implementation("androidx.room:room-runtime:2.4.0")
    implementation("androidx.room:room-ktx:2.4.0")
    ksp("androidx.room:room-compiler:2.4.0")

    // Location
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0")

    implementation("com.google.android.material:material:1.5.0-beta01")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0-RC")
    testImplementation("com.google.truth:truth:1.1.3")

    // AndroidX Test - JVM testing
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("org.robolectric:robolectric:4.7.2")
    testImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    // AndroidX Test - Instrumented testing
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")

    // Dependencies for Android instrumented unit tests
    androidTestImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.fragment:fragment-testing:1.4.0")
    implementation("androidx.test:core-ktx:1.4.0")

    // Dependencies for Android instrumented unit tests
    androidTestImplementation("org.mockito:mockito-core:4.1.0")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    // runBlockingTest replacement
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0-RC")
    androidTestImplementation("com.google.truth:truth:1.1.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.40.2")
    ksp("com.google.dagger:hilt-compiler:2.40.2")
    // For instrumented tests - with Kotlin
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.40.2")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.40.2")

//
//
//    "baselineProfile"(project(":baselineprofile"))
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.core.splashscreen)
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.material3.windowsizeclass)
//    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.material3.adaptive.android)
//    implementation(libs.androidx.profileinstaller)
//
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//    debugImplementation(libs.leakcanary.android)
//
//    // Dagger-Hilt
//    implementation(libs.hilt.android)
//    ksp(libs.hilt.compiler)
//    implementation(libs.hilt.navigation.compose)
//    kspAndroidTest(libs.hilt.android.compiler)
//
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//
//    implementation(libs.coil)
//    implementation(libs.coil.gif)
//
//    // Ktor as replacement of Retrofit
//    implementation(libs.ktor.client.cio)
//    implementation(libs.ktor.client.content.negotiation)
//    implementation(libs.ktor.serialization.kotlinx.json)
//    implementation(libs.kotlinx.datetime)
//
//    // Room 2
//    implementation(libs.androidx.room.runtime)
//    implementation(libs.androidx.room.ktx)
//    ksp(libs.androidx.room.compiler)
//
//    implementation(libs.androidx.legacy.support.v4)
//    implementation(libs.androidx.lifecycle.extensions)
//
//    implementation(libs.kotlinx.coroutines.android)
//    implementation(libs.androidx.datastore.preferences)
//    implementation(libs.timber)
//
//    // testing
//    testImplementation(libs.junit)
//    testImplementation(libs.androidx.test.core.ktx)
//    testImplementation(libs.kotlinx.coroutines.test)
//    testImplementation(libs.robolectric)
//    testImplementation(libs.mockk.android)
//    testImplementation(libs.kotest.assertions.core)
//
//    // For instrumented tests - with Kotlin
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.hilt.android.testing)
//    androidTestImplementation(libs.androidx.test.rules)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    androidTestImplementation(libs.androidx.uiautomator)
//    androidTestImplementation(libs.kotest.assertions.core)
//    androidTestImplementation(libs.kotlinx.coroutines.test)
//    androidTestImplementation(libs.hilt.android.testing)
//    androidTestImplementation(libs.mockk.android)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    android.set(true)
    ignoreFailures.set(true)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
}

tasks.named("preBuild") {
    dependsOn(tasks.named("ktlintFormat"))
}

kover {
    reports {
        // common filters for all reports of all variants
        filters {
            // exclusions for reports
            excludes {
                // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
                classes(
                    listOf(
                        "com.example.android.politicalpreparedness.App",
                        "com.example.android.politicalpreparedness.*.*MembersInjector",
                        "com.example.android.politicalpreparedness.*.*Factory",
                        "com.example.android.politicalpreparedness.*.*HiltModules*",
                        "com.example.android.politicalpreparedness.data.source.local.*_Impl*",
                        "com.example.android.politicalpreparedness.data.source.local.*Impl_Factory",
                        "com.example.android.politicalpreparedness.BR",
                        "com.example.android.politicalpreparedness.BuildConfig",
                        "com.example.android.politicalpreparedness.Hilt*",
                        "com.example.android.politicalpreparedness.*.Hilt_*",
                        "com.example.android.politicalpreparedness.ComposableSingletons*",
                        "*Fragment",
                        "*Fragment\$*",
                        "*Activity",
                        "*Activity\$*",
                        "*.BuildConfig",
                        "*.DebugUtil",
                    ),
                )
                // excludes all classes located in specified package and it subpackages, wildcards '*' and '?' are available
                packages(
                    listOf(
                        "com.example.android.politicalpreparedness.di",
                        "com.example.android.politicalpreparedness.ui.components",
                        "com.example.android.politicalpreparedness.ui.destinations",
                        "com.example.android.politicalpreparedness.ui.navigation",
                        "com.example.android.politicalpreparedness.ui.previewparameter",
                        "com.example.android.politicalpreparedness.ui.theme",
                        "com.example.android.politicalpreparedness.ui.utils",
                        "androidx",
                        "dagger.hilt.internal.aggregatedroot.codegen",
                        "hilt_aggregated_deps",
                    ),
                )
            }
        }
    }
}
