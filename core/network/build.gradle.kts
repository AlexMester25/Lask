plugins {
    id("myapp.android.library")
    id("myapp.ktor")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.network"

    defaultConfig {
        // Добавь в local.properties: NEWS_API_KEY=your_key_here
        val newsApiKey = project.findProperty("NEWS_API_KEY")?.toString()
            ?: System.getenv("NEWS_API_KEY")
            ?: ""
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
    }
}

dependencies {
    implementation(project(":core:models"))

    api(libs.bundles.ktor)
    api(libs.kotlinx.serialization.json)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}