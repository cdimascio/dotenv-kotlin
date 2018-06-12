package io.github.cdimascio.dotenv.internal

import io.github.cdimascio.dotenv.DotEnvException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

internal class DotenvReader(
        private val directory: String,
        private val filename: String
) {
    fun read(): Stream<String> {
        var dir = directory.replace("""\\""".toRegex(), "/")
        dir = if (dir.endsWith("/")) dir.substring(0, dir.length - 1) else dir
        dir = if (dir.endsWith(".env")) dir.substring(0, dir.length - 4) else dir
        val location = "$dir/$filename"
        val path = if (
                location.toLowerCase().startsWith("file:") ||
                location.toLowerCase().startsWith("android.resource:")
        ) {
            Paths.get(URI.create(location))
        } else {
            Paths.get(location)
        }

        return if (Files.exists(path)) Files.lines(path)
            else try {
                ClasspathHelper.loadFileFromClasspath(location)
            } catch (e: DotEnvException) {
                val cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize()
                val cwdMessage = if (!path.isAbsolute) "(working directory: $cwd)" else ""
                e.addSuppressed(DotEnvException("Could not find $path on the file system $cwdMessage"))
                throw e
        }
    }
}