plugins {
    id("myapp.android.application")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.ktor")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.lask"

    defaultConfig {
        applicationId = "dev.alexmester.lask"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":feature:posts"))
    implementation(project(":feature:users"))


}