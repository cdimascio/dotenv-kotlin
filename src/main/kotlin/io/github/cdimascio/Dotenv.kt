package io.github.cdimascio

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors


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
    private var directoryPath = System.getProperty("user.home")
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

    override fun get(envVar: String): String? = map[envVar] ?: System.getenv(envVar)
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
        val path = try {
            PathResolver.resolve(directory, filename)
        } catch (e: DotEnvException) {
            if (throwIfMissing) throw e
            else return listOf()
        }

        val lines =
                try { Files.lines(path) }
                catch (e: Exception) {
                    throw DotEnvException(e)
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

private object PathResolver {
    fun resolve(directory: String, filename: String): Path {
        val isFullPath = directory.endsWith(filename)
        val useFileScheme = directory.toLowerCase().startsWith("file:")
        val fullPath = if (isFullPath) directory else "$directory${File.separator}$filename"
        var path = if (useFileScheme) Paths.get(URI.create(fullPath)) else Paths.get(fullPath)

        if (!Files.exists(path, *arrayOfNulls<LinkOption>(0))) {
            path = javaClass.classLoader.getResource(fullPath)?.let {
                Paths.get(it.toURI())
            }
        }
        if (path === null) {
            println("$path")
            throw DotEnvException("${path} not found")
        }
        return path
    }
}
