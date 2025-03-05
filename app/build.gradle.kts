@file:Suppress("DSL_SCOPE_VIOLATION")
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application) apply true
    alias(libs.plugins.kotlin.android) apply true
    alias(libs.plugins.kotlin.ksp) apply true
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("com.autonomousapps.dependency-analysis") version "2.1.4"
}

apply(plugin = libs.plugins.android.application.get().pluginId)
apply(plugin = libs.plugins.kotlin.android.get().pluginId)
apply(plugin = libs.plugins.kotlin.ksp.get().pluginId)

abstract class GitHashService @Inject constructor(private val execOps: ExecOperations) {
    fun getCommitHash(): String {
        val stdout = ByteArrayOutputStream()
        execOps.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = stdout
        }
        return stdout.toString().trim()
    }
    fun commitCount(): String {
        val stdout = ByteArrayOutputStream()
        execOps.exec {
            commandLine("git", "rev-list", "--count", "HEAD")
            standardOutput = stdout
        }
        return stdout.toString().trim()
    }
}

android {
    namespace = property.project.app.packageName
    compileSdk = 35

    lint {
        baseline = file("lint-baseline.xml")
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = true
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val name = "OShin"
                val abi = output.getFilter("ABI") ?: "all"
                val version = variant.versionName
                val versionCode = variant.versionCode
                val outputFileName = "${name}_${abi}_${"v"}${version}(${versionCode}).apk"
                output.outputFileName = outputFileName
            }
    }

    val gitHashService = project.objects.newInstance(GitHashService::class.java)
    val number = gitHashService.commitCount().toInt()
    defaultConfig {
        applicationId = property.project.app.packageName
        minSdk = property.project.android.minSdk
        targetSdk = property.project.android.targetSdk
        versionName = property.project.app.versionName+"."+number+"."+gitHashService.getCommitHash()
        versionCode = number
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    val keystoreFile = System.getenv("KEYSTORE_PATH")
    signingConfigs {
        if (keystoreFile != null) {
            create("ci") {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV4Signing = true
            }
        } else {
            create("release") {
                enableV4Signing = true
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName(if (keystoreFile != null) "ci" else "release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isJniDebuggable = false
            isCrunchPngs = true
            multiDexEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "21"
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
    buildToolsVersion = "35.0.0"
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
    androidResources {
        ignoreAssetsPattern = "!*.ttf:!*.json:!*.bin"
        noCompress += listOf("zip", "txt", "raw")
    }
    // TODO Please visit https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject
    // TODO 请参考 https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject
    // androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    //implementation(fileTree("libs") { include("*.jar") })
    implementation(libs.common)
    implementation(libs.umsdk.asms)
    implementation(libs.umsdk.uyumao)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.okhttp)
    implementation(libs.lottie.compose)
    implementation(libs.ezxhelper)
    runtimeOnly(libs.androidx.room.runtime)
    implementation(libs.androidx.palette.ktx)
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
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    compileOnly(de.robv.android.xposed.api)
    implementation(com.highcapable.yukihookapi.api)
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
