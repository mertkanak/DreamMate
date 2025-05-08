pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // uyarı çözülür
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DreamMate"
include(":app")