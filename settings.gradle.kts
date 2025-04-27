pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // 可选，但推荐
    repositories {
        google()          // 如果你需要 Google 的库 (例如 Android)
        mavenCentral()    // **必须添加**，用于 Kotlin 标准库、Retrofit 等
       maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "DmNet"


