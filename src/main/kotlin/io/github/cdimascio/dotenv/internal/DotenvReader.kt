package io.github.cdimascio.dotenv.internal

import java.net.URI
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
        val path = if (location.toLowerCase().startsWith("file:")) {
            Paths.get(URI.create(location))
        } else {
            Paths.get(location)
        }
        return if (Files.exists(path)) Files.lines(path)
        else ClasspathHelper.loadFileFromClasspath(location)
    }
}