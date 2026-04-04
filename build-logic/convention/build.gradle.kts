plugins {
    `kotlin-dsl`
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "myapp.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "myapp.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "myapp.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "myapp.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "myapp.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
        register("koin") {
            id = "myapp.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("ktor") {
            id = "myapp.ktor"
            implementationClass = "KtorConventionPlugin"
        }
        register("room") {
            id = "myapp.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("coil") {
            id = "myapp.coil"
            implementationClass = "CoilConventionPlugin"
        }
        register("datastore") {
            id = "myapp.datastore"
            implementationClass = "DataStoreConventionPlugin"
        }
        register("media3") {
            id = "myapp.media3"
            implementationClass = "Media3ConventionPlugin"
        }
    }
}