pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
        maven("https://repo.maven.apache.org/maven2")
        maven("https://jitpack.io")
        maven {
            url = uri("https://api.xposed.info/")
            content {
                includeGroup("de.robv.android.xposed")
            }
        }
    }
}

plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.5"
}
sweetProperty {
    rootProject { all { isEnable = false } }
}
rootProject.name = "OShin"
include(":app")
