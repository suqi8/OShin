plugins {
    autowire(libs.plugins.android.application) apply false
    autowire(libs.plugins.kotlin.ksp) apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}
