// 声明项目构建所需的 Gradle 插件。
plugins {
    // 通过 Gradle 版本目录（Version Catalog）的别名方式引用插件，以实现集中管理。
    alias(libs.plugins.android.application) // Android 应用程序插件，用于构建 .apk 文件。
    alias(libs.plugins.kotlin.android)      // 提供 Kotlin 语言在 Android 平台上的支持。
    alias(libs.plugins.ksp)                 // Kotlin 符号处理（KSP）插件，用于执行编译时代码生成。
    alias(libs.plugins.kotlin.compose)      // Jetpack Compose 编译器插件，用于处理 @Composable 注解。
    id("com.google.dagger.hilt.android")
}

/**
 * Git 版本信息提供者。
 *
 * 通过 Gradle 的 Provider API 实现构建信息的延迟化配置（Lazy Configuration）。
 * `git` 命令仅在配置属性（如 `versionCode`）被实际需要时执行，
 * 以此优化 Gradle 在配置阶段（Configuration Phase）的性能。
 */
// 获取当前 Git 提交的短哈希值。
val gitCommitHashProvider = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.map { it.trim() }

// 获取从项目起始到当前 HEAD 的总提交次数。
val gitCommitCountProvider = providers.exec {
    commandLine("git", "rev-list", "--count", "HEAD")
}.standardOutput.asText.map { it.trim() }

// Android 项目的核心配置。
android {
    namespace = "com.suqi8.oshin" // 定义应用的包名，用于生成 R 类和 Manifest。
    compileSdk = 36               // 指定用于编译应用的 Android API 版本。

    // 默认配置，应用于所有的构建变体（Build Variant）。
    defaultConfig {
        applicationId = "com.suqi8.oshin" // 应用程序在设备和应用商店上的唯一标识符。
        minSdk = 36                       // 应用可以运行的最低 Android API 级别。
        targetSdk = 36                    // 应用设计和测试所基于的目标 Android API 级别。

        // 动态设置版本信息。
        // .getOrElse() 提供了一个回退值，确保在非 Git 环境下构建的健壮性。
        versionCode = gitCommitCountProvider.map { it.toInt() }.getOrElse(1)
        versionName = gitCommitCountProvider.zip(gitCommitHashProvider) { count, hash ->
            "16.1.$count.$hash" // 版本名格式：主版本.次版本.提交总数.提交哈希
        }.getOrElse("16.local") // 在 Git 不可用时使用的默认版本名。

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // 指定仪器测试的运行器。
        vectorDrawables {
            useSupportLibrary = true // 为 API 21 以下的设备启用对矢量图的支持。
        }
    }

    // --- ABI 拆分配置 ---
    // 此配置块用于指示 Gradle 为不同的 CPU 架构生成独立的 APK。
    splits {
        abi {
            isEnable = true          // 1. 启用 ABI 拆分
            reset()                // 2. 清除默认设置 (如 x86, mips 等)
            include("arm64-v8a")     // 3. 只包含 64 位 v8a 架构
            isUniversalApk = false     // 4. 不再生成通用 (universal/all) APK
        }
    }

    // 配置 APK 输出文件名。
    // 注意：此处使用已废弃的 `applicationVariants.all` API。
    // 这是为了兼容当前构建环境，以确保 APK 文件名自定义功能的稳定性。
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val name = "OShin"
            // 从输出过滤器中获取 ABI（Application Binary Interface）名称。
            val abi = outputImpl.filters.find { it.filterType == "ABI" }?.identifier ?: "all"
            val version = variant.versionName
            val versionCode = variant.versionCode
            val outputFileName = "${name}_${abi}_v${version}(${versionCode}).apk"
            outputImpl.outputFileName = outputFileName
        }
    }

    // 配置应用的签名信息。
    signingConfigs {
        val keystoreFile = System.getenv("KEYSTORE_PATH")
        val isCiBuild = keystoreFile != null

        // 若检测到 CI/CD 环境变量，则创建用于持续集成的签名配置。
        if (isCiBuild) {
            create("ci") {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV4Signing = true // 启用 APK 签名方案 v4，以支持增量安装等优化。
            }
        }
        // 创建一个通用的 "release" 签名配置，用于本地构建或在 CI/CD 环境之外的场景。
        create("release") {
            enableV4Signing = true
        }
    }

    // 配置不同的构建类型，如 "release" 和 "debug"。
    buildTypes {
        release {
            val keystoreFile = System.getenv("KEYSTORE_PATH")
            val isCiBuild = keystoreFile != null
            // 根据是否存在 CI 环境变量来决定使用哪个签名配置。
            signingConfig = signingConfigs.getByName(if (isCiBuild) "ci" else "release")

            // 通过 `buildConfigField` 在 `BuildConfig.java` 中生成一个常量。
            val buildTag = if (isCiBuild) "CI Build" else "Release"
            buildConfigField("String", "BUILD_TYPE_TAG", "\"$buildTag\"")

            isMinifyEnabled = true      // 启用 R8/ProGuard 进行代码压缩、优化和混淆。
            isShrinkResources = true    // 启用资源缩减，移除未被引用的资源文件。
            isDebuggable = false        // 发布版本禁止调试。
            isJniDebuggable = false     // 禁止对 JNI (C/C++) 代码进行调试。
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            buildConfigField("String", "BUILD_TYPE_TAG", "\"Debug\"")
        }
    }

    // Java/Kotlin 编译选项。
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21 // 设置 Java 源代码的语言级别。
        targetCompatibility = JavaVersion.VERSION_21 // 设置生成的 Java 字节码的目标 JVM 版本。
    }

    // 配置 Kotlin 编译器选项。
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) // 设置 Kotlin 编译输出的 JVM 目标版本。
            freeCompilerArgs.addAll(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions"
            )
        }
    }

    // 启用或禁用特定的构建功能。
    buildFeatures {
        buildConfig = true // 启用 `BuildConfig.java` 的自动生成。
        viewBinding = true // 启用视图绑定功能。
        compose = true     // 启用 Jetpack Compose 支持。
    }

    // Jetpack Compose 相关的编译器配置。
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    // APK 打包相关的配置。
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin-tooling-metadata.json"
        }
    }

    // Android 资源处理相关的配置。
    androidResources {
        ignoreAssetsPattern = "!*.ttf:!*.json:!*.bin"
        noCompress += listOf("zip", "txt", "raw", "png")
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xcontext-parameters"
            )
        }
    }

    // Lint 静态代码分析工具的配置。
    lint {
        baseline = file("lint-baseline.xml") // 设置一个基线文件，用于忽略已存在的 Lint 问题。
    }
}

// 依赖项声明块
dependencies {
    // ------------------- AndroidX & Jetpack 核心库 -------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation.layout)
    ksp(libs.hilt.android.compiler)

    // ------------------- Jetpack Compose UI -------------------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // ------------------- Compose 生态第三方库 -------------------
    implementation(libs.accompanist.flowlayout)
    implementation(libs.airbnb.lottie.compose)
    implementation(libs.coil.compose)
    implementation(libs.haze)
    implementation(libs.shimmer.compose)
    implementation(libs.toolbar.compose)
    implementation(libs.expandablebottombar)
    implementation(libs.neumorphism.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.capsule)
    implementation(libs.multiplatform.markdown.renderer.android)
    implementation(libs.multiplatform.markdown.renderer.coil3)
    implementation(libs.multiplatform.markdown.renderer.code)

    // ------------------- 底层与工具库 -------------------
    implementation(libs.luckypray.dexkit)
    implementation(libs.xxpermissions)
    implementation(libs.squareup.okhttp)
    implementation(libs.coil.network.okhttp)
    implementation(libs.gson)
    implementation(libs.drawabletoolbox)
    implementation(libs.miuix)
    implementation(libs.mmkv)

    // ------------------- Hook API 相关 -------------------
    implementation(libs.ezxhelper)
    compileOnly(libs.xposed.api)
    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp.xposed)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    // ------------------- Room 数据库 -------------------
    runtimeOnly(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // ------------------- Umeng (友盟) SDK -------------------
    implementation(libs.umeng.common)
    implementation(libs.umeng.asms)
    implementation(libs.umeng.uyumao)

    // ------------------- 测试相关库 -------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
