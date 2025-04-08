pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
        maven("https://repo.maven.apache.org/maven2")
        maven("https://jitpack.io")
        
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
