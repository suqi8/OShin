
import com.android.build.gradle.internal.dsl.SigningConfig
import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application") version "8.11.0"
    id("org.jetbrains.kotlin.android") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

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
    namespace = "com.suqi8.oshin"
    compileSdk = 36

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
        applicationId = "com.suqi8.oshin"
        minSdk = 35
        targetSdk = 36
        versionName = "15.5"+"."+number+"."+gitHashService.getCommitHash()
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
            buildConfigField("String", "BUILD_TYPE_TAG", "\"${if ((signingConfig as SigningConfig).name == "ci") "CI Build" else "Release"}\"")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isJniDebuggable = false
            isCrunchPngs = true
            multiDexEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            buildConfigField("String", "BUILD_TYPE_TAG", "\"Debug\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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
    ndkVersion = "29.0.13113456 rc1"
    buildToolsVersion = "36.0.0"
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
        noCompress += listOf("zip", "txt", "raw", "png")
    }
    // TODO Please visit https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject
    // TODO 请参考 https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject
    // androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.dexkit)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.toolbar.compose)
    implementation(libs.xxpermissions)
    implementation(libs.common)
    implementation(libs.umsdk.asms)
    implementation(libs.umsdk.uyumao)
    implementation(libs.okhttp)
    implementation(libs.lottie.compose)
    implementation(libs.ezxhelper)
    runtimeOnly(libs.androidx.room.runtime)
    implementation(libs.androidx.palette.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.haze)
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
    implementation(libs.androidx.navigation.runtime.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    compileOnly(libs.api)
    implementation(libs.yukihookapi.api)
    ksp(libs.ksp.xposed)
    implementation(libs.drawabletoolbox)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
}
