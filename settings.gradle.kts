// `pluginManagement` 用于配置 Gradle 插件的仓库源。
// Gradle 将从此代码块中定义的仓库中查找 `plugins {}` 块中声明的插件。
pluginManagement {
    repositories {
        gradlePluginPortal() // Gradle 官方插件门户。
        mavenCentral()       // Maven 中央仓库。
        google()             // Google 的 Maven 仓库，用于存放 Android 相关库和插件。
        maven("https://jitpack.io") // JitPack 仓库，用于轻松构建任何 GitHub/GitLab 项目。
        // Xposed 框架的专用 Maven 仓库。
        maven {
            url = uri("https://api.xposed.info/")
            content {
                // 仅从此仓库中查找属于 "de.robv.android.xposed" 组的依赖。
                includeGroup("de.robv.android.xposed")
            }
        }
    }
}

// `dependencyResolutionManagement` 用于集中管理所有模块的依赖项仓库。
dependencyResolutionManagement {
    // 设置仓库模式为 FAIL_ON_PROJECT_REPOS，禁止在子模块的 build.gradle 文件中单独定义仓库。
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        // Xposed 框架的专用 Maven 仓库。
        maven {
            url = uri("https://api.xposed.info/")
            content {
                includeGroup("de.robv.android.xposed")
            }
        }
    }
}

// 设置根项目的名称。
rootProject.name = "OShin"
// 包含 :app 模块，使其成为项目构建的一部分。
include(":app")
