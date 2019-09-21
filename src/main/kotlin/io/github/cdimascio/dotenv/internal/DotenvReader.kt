/**
 * Copyright (c) Carmine DiMascio 2017 - 2019
 * License: MIT
 */
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
    /**
     * Reads the contents of the .env file
     */
    fun read(): Stream<String> {
        val dir = directory.replace("""\\""".toRegex(), "/").removeSuffix(".env").removeSuffix("/")

        val location = "$dir/$filename"
        val path = if (
                location.startsWith("file:", ignoreCase = true) ||
                location.startsWith("android.resource:", ignoreCase = true)
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
