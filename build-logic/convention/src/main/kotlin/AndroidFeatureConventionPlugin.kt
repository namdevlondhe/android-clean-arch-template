import dev.namdev.template.buildlogic.lib
import dev.namdev.template.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * A feature module = Android library + Compose + Hilt, and it may only
 * depend on core:domain / core:designsystem / core:model (never on core:data).
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("template.android.library")
        pluginManager.apply("template.android.compose")
        pluginManager.apply("template.android.hilt")

        dependencies {
            add("implementation", project(":core:model"))
            add("implementation", project(":core:domain"))
            add("implementation", project(":core:designsystem"))

            add("implementation", libs.lib("androidx-lifecycle-runtime-compose"))
            add("implementation", libs.lib("androidx-lifecycle-viewmodel-compose"))
            add("implementation", libs.lib("androidx-navigation-compose"))
            add("implementation", libs.lib("hilt-navigation-compose"))

            add("testImplementation", libs.lib("turbine"))
        }
    }
}
