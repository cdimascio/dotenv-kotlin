package io.github.cdimascio

import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


interface Dotenv {
    companion object Instance {
        fun configure(): DotenvBuilder = DotenvBuilder()
    }

    operator fun get(envVar: String): String?
}

class DotEnvException : Exception {
    constructor(message: String) : super(message)
    constructor(throwable: Throwable) : super(throwable)
}

class DotenvBuilder internal constructor() {
    private var filename = ".env"
    private var directoryPath = "" //System.getProperty("user.home")
    private var throwIfMissing = true
    private var throwIfMalformed = true

    fun directory(path: String = directoryPath): DotenvBuilder {
        directoryPath = path
        return this
    }

    fun ignoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    fun build(): Dotenv {
        val reader = DotEnvReader(directoryPath, filename, throwIfMalformed, throwIfMissing)
        val env = reader.read()
        return DotenvImpl(env)
    }
}

private class DotenvImpl(envVars: List<Pair<String, String>>) : Dotenv {
    val map = envVars.associateBy({ it.first }, { it.second })

    override fun get(envVar: String): String? = System.getenv(envVar) ?: map[envVar]
}

private class DotEnvReader(
        val directory: String,
        val filename: String = ".env",
        val throwIfMalformed: Boolean,
        val throwIfMissing: Boolean
) {
    val isWhiteSpace = { s: String -> """^\s*${'$'}""".toRegex().matches(s) }
    val isComment = { s: String -> s.startsWith("#") || s.startsWith("""//""") }
    val parseLine = { s: String -> """^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$""".toRegex().matchEntire(s) }

    fun read() = parse()

    private fun parse(): List<Pair<String, String>> {
        val lines = try {
            DotenvParser.parse(directory, filename)
        } catch (e: DotEnvException) {
            if (throwIfMissing) throw e
            else return listOf()
        }.collect(Collectors.toList())

        return lines
                .map { it.trim() }
                .filter { !isWhiteSpace(it) }
                .filter { !isComment(it) }
                .mapNotNull {
                    val match = parseLine(it)
                    if (match != null) {
                        val (key, _, value) = match.destructured
                        Pair(key, value)
                    } else {
                        if (throwIfMalformed) throw DotEnvException("Malformed entry: $it")
                        else null
                    }
                }
    }
}

private object DotenvParser {
    fun parse(directory: String, filename: String): Stream<String> {
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

private object ClasspathHelper {
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
                throw DotEnvException("Could not read $location from the classpath")
            }
        }
        throw DotEnvException("Could not find $location on the classpath")
    }
}