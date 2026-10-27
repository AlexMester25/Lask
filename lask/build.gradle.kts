import java.util.Properties

plugins {
    id("myapp.android.application")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.coil")
//    alias(libs.plugins.baselineprofile)
}
val localProperties = Properties().apply {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) propsFile.reader().use { load(it) }
}

fun secret(name: String): String? = localProperties.getProperty(name) ?: System.getenv(name)

android {
    namespace = "dev.alexmester.lask"
    signingConfigs {
        create("release") {
            secret("RELEASE_STORE_FILE")?.let { path ->
                storeFile = rootProject.file(path)
                storePassword = secret("RELEASE_STORE_PASSWORD")
                keyAlias = secret("RELEASE_KEY_ALIAS")
                keyPassword = secret("RELEASE_KEY_PASSWORD")
            }
        }
    }
    defaultConfig {
        applicationId = "dev.alexmester.lask"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            matchingFallbacks += listOf("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:models"))
    implementation(project(":core:utils"))
    implementation(project(":core:platform"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))

    implementation(project(":feature:news_feed:impl"))
    implementation(project(":feature:explore:impl"))
    implementation(project(":feature:article_detail:impl"))
    implementation(project(":feature:bookmarks:impl"))
    implementation(project(":feature:profile:impl"))
    implementation(project(":feature:search:impl"))
    implementation(project(":feature:typed_article_list:impl"))
    implementation(project(":feature:interests:impl"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)

//    "baselineProfile"(project(":macrobenchmark"))
}
// Настройки генерации профиля
//baselineProfile {
//    baselineProfileOutputDir = "src/main"
//    automaticGenerationDuringBuild = false
//    saveInSrc = true
//    mergeIntoMain = true
//}