import java.io.ByteArrayOutputStream
import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream

plugins {
    autowire(libs.plugins.android.application)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.kotlin.ksp)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
}

fun getGitCommitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = "git rev-parse --short HEAD".split(" ")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun getAndIncrementBuildNumber(): Int? {
    val propertiesFile = file("version.properties")
    val properties = Properties()

    // Load existing properties
    if (propertiesFile.exists()) {
        properties.load(FileInputStream(propertiesFile))
    } else {
        // If file doesn't exist, create a new one
        properties["BUILD_NUMBER"] = "1"
    }

    // Get the current build number
    val buildNumber = properties["BUILD_NUMBER"].toString().toInt()

    // Increment the build number
    properties["BUILD_NUMBER"] = (buildNumber + 1).toString()

    // Save the updated build number back to the properties file
    properties.store(FileOutputStream(propertiesFile), null)

    return buildNumber
}

android {
    namespace = property.project.app.packageName
    compileSdk = 34

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val name = "OPatch"
                var abi = output.getFilter(com.android.build.OutputFile.ABI)
                if (abi == null) abi = "all" //兼容
                val version = variant.versionName
                val versionCode = variant.versionCode
                val outputFileName = "${name}_${abi}_${"v"}${version}(${versionCode}).apk"
                output.outputFileName = outputFileName
            }
    }

    val number = getAndIncrementBuildNumber()
    defaultConfig {
        applicationId = property.project.app.packageName
        minSdkVersion(rootProject.extra["defaultMinSdkVersion"] as Int)
        targetSdk = property.project.android.targetSdk
        versionName = property.project.app.versionName+".b"+number+"."+getGitCommitHash()
        versionCode = number
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    lint { checkReleaseBuilds = false }
    ndkVersion = "27.0.11718014 rc1"
    buildToolsVersion = "34.0.0"
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // TODO Please visit https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject
    // TODO 请参考 https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject
    // androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.haze)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.miuix)
    implementation(libs.gson)
    implementation(libs.compose.shimmer)
    implementation(libs.expandablebottombar)
    implementation(libs.composeneumorphism)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx.v282)
    implementation(libs.androidx.activity.compose.v190)
    implementation(platform(libs.androidx.compose.bom.v20240600))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    compileOnly(de.robv.android.xposed.api)
    implementation(com.highcapable.yukihookapi.api)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    ksp(com.highcapable.yukihookapi.ksp.xposed)
    implementation(com.github.duanhong169.drawabletoolbox)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    implementation(com.google.android.material.material)
    implementation(androidx.constraintlayout.constraintlayout)
    testImplementation(junit.junit)
    androidTestImplementation(androidx.test.ext.junit)
    androidTestImplementation(androidx.test.espresso.espresso.core)
}
