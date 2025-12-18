/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 */

import com.android.build.api.dsl.ManagedVirtualDevice
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
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
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

// Configuration
val productApkName = "politicalpreparedness"
val productNamespace = "com.example.android.politicalpreparedness"
val isRunningOnCI = System.getenv("CI") == "true"

android {
    namespace = productNamespace

    setupSdkVersionsFromVersionCatalog()
    setupSigningAndBuildTypes()
    setupPackagingResourcesDeduplication()

    defaultConfig {
        applicationId = productNamespace

        testInstrumentationRunner = "$productNamespace.ui.test.CustomTestRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    androidResources.localeFilters.add("en")

    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        managedDevices {
            allDevices {
                create<ManagedVirtualDevice>("pixel2Api35") {
                    device = "Pixel 2"
                    apiLevel = 35
                    systemImageSource = "aosp-atd"
                    // testedAbi = "arm64-v8a" // better performance on CI and Macs
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Api key: try environment variable first, then keystore properties file
    val civicApiKeyEnv = System.getenv("CIVIC_API_KEY")
    val keystorePropertiesFile = file("../../keystore.properties")

    if (!civicApiKeyEnv.isNullOrBlank()) {
        println("⚠\uFE0F Civic API key: imported from environment variable")
        defaultConfig.buildConfigField(
            type = "String",
            name = "CIVIC_API_KEY",
            value = System.getenv("CIVIC_API_KEY"),
        )
    } else if (keystorePropertiesFile.exists()) {
        println("⚠\uFE0F Civic API key: Imported from keystore")
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
        println("⚠\uFE0F Civic API key: not found. App may not function correctly.")
        defaultConfig.buildConfigField(
            "String",
            "CIVIC_API_KEY",
            "\"\"",
        )
    }
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    jvmToolchain(17)
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.reflect)
    implementation(libs.jetbrains.kotlin.stdlib)

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
    implementation(libs.androidx.uiautomator)
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

    // AndroidX Test - JVM testing
    testImplementation(libs.core.ktx)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.androidx.core.testing)

    // AndroidX Test - Instrumented testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependencies for Android instrumented unit tests
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.fragment.testing)
    debugImplementation(libs.androidx.fragment.testing)
    implementation(libs.core.ktx)

    // Dependencies for Android instrumented unit tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.dexmaker.mockito)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.espresso.idling.resource)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    // Compose - for future use
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks {
    check { dependsOn("detekt") }
    preBuild { dependsOn("formatKotlin") }
}

detekt { parallel = true }

kover {
    useJacoco()
    reports.filters.excludes {
        packages(
            "$productNamespace.di*",
            "$productNamespace.ui.components",
            "$productNamespace.ui.destinations",
            "$productNamespace.ui.navigation",
            "$productNamespace.ui.previewparameter",
            "$productNamespace.ui.theme",
            "$productNamespace.ui.utils",
            "androidx",
            "dagger.hilt.internal.aggregatedroot.codegen",
            "hilt_aggregated_deps",
        )

        classes(
            "$productNamespace.App",
            "$productNamespace.*.*MembersInjector",
            "$productNamespace.*.*Factory",
            "$productNamespace.*.*HiltModules*",
            "$productNamespace.data.source.local.*_Impl*",
            "$productNamespace.data.source.local.*Impl_Factory",
            "$productNamespace.BR",
            "$productNamespace.BuildConfig",
            "$productNamespace.Hilt*",
            "$productNamespace.*.Hilt_*",
            "$productNamespace.ComposableSingletons*",
            "dagger.hilt.internal.aggregatedroot.codegen.*",
            "hilt_aggregated_deps.*",
            "*Fragment",
            "*Fragment\$*",
            "*Activity",
            "*Activity\$*",
            "*.databinding.*",
            "*.BuildConfig",
            "*.DebugUtil",
        )
    }
}

// Gradle Build Utilities
private fun BaseAppModuleExtension.setupSdkVersionsFromVersionCatalog() {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
}

private fun BaseAppModuleExtension.setupPackagingResourcesDeduplication() {
    packaging.resources {
        excludes.addAll(
            listOf(
                "META-INF/*.md",
                "META-INF/proguard/*",
                "META-INF/*.kotlin_module",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/LICENSE-notice.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.properties",
                "/*.properties",
            ),
        )
    }
}

private fun BaseAppModuleExtension.setupSigningAndBuildTypes() {
    val releaseSigningConfigName = "releaseSigningConfig"
    val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
    val baseName = "$productApkName-${libs.versions.versionName.get()}-$timestamp"
    val isReleaseBuild = gradle.startParameter.taskNames.any {
        it.contains("Release", ignoreCase = true)
                || it.contains("Bundle", ignoreCase = true)
                || it.equals("build", ignoreCase = true)
    }

    extensions.configure<BasePluginExtension> { archivesName.set(baseName) }

    signingConfigs.create(releaseSigningConfigName) {
        // Only initialise the signing config when a Release or Bundle task is being executed.
        // This prevents Gradle sync or debug builds from attempting to load the keystore,
        // which could fail if the keystore or environment variables are not available.
        // SigningConfig itself is only wired to the 'release' build type, so this guard avoids unnecessary setup.
        if (isReleaseBuild) {
            val keystorePropertiesFile = file("../../keystore.properties")

            if (isRunningOnCI || !keystorePropertiesFile.exists()) {
                println("⚠\uFE0F Signing Config: using environment variables")
                keyAlias = System.getenv("CI_ANDROID_KEYSTORE_ALIAS")
                keyPassword = System.getenv("CI_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
                storeFile = file(System.getenv("KEYSTORE_LOCATION"))
                storePassword = System.getenv("CI_ANDROID_KEYSTORE_PASSWORD")
            } else {
                println("⚠\uFE0F Signing Config: using keystore properties")
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
            }
        } else {
            println("⚠\uFE0F Signing Config: not created for non-release builds.")
        }
    }

    buildTypes {
        fun setOutputFileName() {
            applicationVariants.all {
                outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        val outputFileName = "$productApkName-$name-$versionName-$timestamp.apk"
                        output.outputFileName = outputFileName
                    }
            }
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isDebuggable = true
            setOutputFileName()
        }

        create("benchmark") {
            initWith(getByName("release"))
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            matchingFallbacks.add("release")
            setOutputFileName()
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
            signingConfig = signingConfigs.getByName(name = releaseSigningConfigName)
            setOutputFileName()
        }
    }
}
