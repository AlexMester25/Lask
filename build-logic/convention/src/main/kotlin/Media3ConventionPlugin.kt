import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies

class Media3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            dependencies {
                add("implementation", libs.findLibrary("media3.exoplayer").get())
                add("implementation", libs.findLibrary("media3.ui").get())
                add("implementation", libs.findLibrary("media3.common").get())
            }
        }
    }
}