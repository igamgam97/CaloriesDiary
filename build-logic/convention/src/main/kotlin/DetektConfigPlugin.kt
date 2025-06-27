import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConfigPlugin : Plugin<Project> {
    companion object {
        private val excludeFiles: List<String> = listOf("**/build/**", "**/generated/**")
        private val includeFiles: List<String> = listOf("**/*.kts", "**/*.kt")
    }

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.gitlab.arturbosch.detekt")
            configureDetektPlugin()
            configureDetektTasks()
            configureDetektBaselineTasks()
            addDependencies()
        }
    }

    private fun Project.configureDetektPlugin() {
        configure<DetektExtension> {
            autoCorrect = true
            parallel = true
            buildUponDefaultConfig = true
            baseline = file("detekt-baseline.xml")
            config.from(rootProject.file("config/detekt/detekt.yml"))
            ignoredBuildTypes = listOf("release")
            source.setFrom(
                "src/main/java",
                "src/main/kotlin",
                "src/test/java",
                "src/test/kotlin",
                "src/androidTest/java",
                "src/androidTest/kotlin",
            )
        }
    }

    private fun Project.configureDetektTasks() {
        tasks.withType<Detekt>().configureEach {
            setSource(files(project.projectDir))
            include(includeFiles)
            exclude(excludeFiles)

            reports {
                html.required.set(true)
                html.outputLocation.set(file("build/reports/detekt/results.html"))
                xml.required.set(false)
                sarif.required.set(false)
                txt.required.set(false)
                md.required.set(false)
            }
        }
    }

    private fun Project.configureDetektBaselineTasks() {
        tasks.withType<DetektCreateBaselineTask>().configureEach {
            include(includeFiles)
            exclude(excludeFiles)
        }
    }

    private fun Project.addDependencies() {
        dependencies {
            add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.5")
            add("detektPlugins", "com.twitter.compose.rules:detekt:0.0.26")
        }
    }
}