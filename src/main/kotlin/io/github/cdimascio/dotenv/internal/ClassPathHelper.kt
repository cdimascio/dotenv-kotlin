package io.github.cdimascio.dotenv.internal

import io.github.cdimascio.dotenv.DotEnvException
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.stream.Stream

internal object ClasspathHelper {
    fun loadFileFromClasspath(location: String): Stream<String> {
        val loader = ClasspathHelper::class.java
        val inputStream: InputStream? =
                loader.getResourceAsStream(location)
                        ?: loader.getResourceAsStream(location)
                        ?: ClassLoader.getSystemResourceAsStream(location)
        if (inputStream != null) {
            try {
                val scanner = Scanner(inputStream, "utf-8")
                val lines = mutableListOf<String>()
                while (scanner.hasNext()) {
                    lines.add(scanner.nextLine())
                }
                return lines.stream()
            } catch (e: IOException) {
                throw DotEnvException("Could not parse $location from the classpath")
            }
        }
        throw DotEnvException("Could not find $location on the classpath")
    }
}