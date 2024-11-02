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

//        testInstrumentationRunner =
//            "com.example.android.politicalpreparedness.ui.test.CustomTestRunner"
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
//    testFixtures {
//        enable = true
//        androidResources = true
//    }

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
    implementation(libs.jetbrains.kotlin.stdlib)
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
    implementation(libs.converter.moshi)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Glide
    implementation(libs.glide)
    ksp(libs.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Location
    implementation(libs.play.services.location)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.material)
    implementation(libs.timber)

    // Dependencies for local unit tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)

    // AndroidX Test - JVM testing
    testImplementation(libs.core.ktx)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.androidx.core.testing)

    // AndroidX Test - Instrumented testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)

    // Dependencies for Android instrumented unit tests
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.fragment.testing)
    debugImplementation(libs.androidx.fragment.testing)
    implementation(libs.core.ktx)

    // Dependencies for Android instrumented unit tests
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.dexmaker.mockito)
    androidTestImplementation(libs.androidx.core.testing)
    implementation(libs.androidx.espresso.idling.resource)
    // runBlockingTest replacement
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.truth)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // For instrumented tests - with Kotlin
    // androidTestImplementation(libs.hilt.android.testing)
    // kspAndroidTest(libs.hilt.android.compiler)
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
