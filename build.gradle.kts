plugins {
    autowire(libs.plugins.android.application) apply false
    autowire(libs.plugins.kotlin.android) apply false
    autowire(libs.plugins.kotlin.ksp) apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
}
val defaultMinSdkVersion by extra(31)
