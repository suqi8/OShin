plugins {
    autowire(libs.plugins.android.application) apply false
    autowire(libs.plugins.kotlin.android) apply false
    autowire(libs.plugins.kotlin.ksp) apply false
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id("com.autonomousapps.dependency-analysis") version "2.1.4"
}
val defaultMinSdkVersion by extra(33)

