import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            // Opt-in only: `./gradlew :lask:assembleRelease -PcomposeCompilerReports=true`.
            // Reports must come from a release (R8/minified) build: debug builds disable
            // Live Literals differently, run unminified and the Compose runtime is not
            // optimized, so stability/skippability numbers would be misleading.
            if (providers.gradleProperty(COMPOSE_REPORTS_PROPERTY).orNull == "true") {
                val reportsDir = layout.buildDirectory.dir("compose_compiler")
                extensions.configure<ComposeCompilerGradlePluginExtension> {
                    reportsDestination.set(reportsDir)
                    metricsDestination.set(reportsDir)
                }
            }

            val extension = extensions.getByType(CommonExtension::class.java)
            extension.buildFeatures.compose = true

            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

            dependencies {
                add("implementation", platform(libs.findLibrary("androidx.compose.bom").get()))
                add("androidTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
                add("implementation", libs.findLibrary("androidx.compose.ui").get())
                add("implementation", libs.findLibrary("androidx.compose.ui.graphics").get())
                add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                add("implementation", libs.findLibrary("androidx.compose.material3").get())
                add("implementation", libs.findLibrary("androidx.compose.material.icons").get())
                add("implementation", libs.findLibrary("androidx.activity.compose").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test.junit4").get())
            }
        }
    }

    private companion object {
        const val COMPOSE_REPORTS_PROPERTY = "composeCompilerReports"
    }
}