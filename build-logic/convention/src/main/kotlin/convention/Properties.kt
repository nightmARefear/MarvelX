package convention

import org.gradle.api.Project
import java.util.Properties

internal fun Project.getProperties(fileName: String): Properties? {
    return try {
        val properties = Properties().apply {
            load(rootProject.file(fileName).reader())
        }
        properties
    } catch (_: Exception) {
        null
    }
}
