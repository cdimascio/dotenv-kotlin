package io.github.cdimascio

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream
import java.io.File
import java.nio.file.Path


interface Dotenv {
    companion object Instance {
        fun configure(): DotenvBuilder = DotenvBuilder()
    }
    operator fun get(envVar: String): String?
}

class DotEnvException : Exception {
    constructor(message: String): super(message)
    constructor(throwable: Throwable): super(throwable)
}

class DotenvBuilder internal constructor() {
    private var filename = ".env"
    private var resources = false
    private var directoryPath = System.getProperty("user.home")
    private var throwIfMissing = true
    private var throwIfMalformed = true

    fun useDirectory(path: String = directoryPath): DotenvBuilder {
        directoryPath = path
        return this
    }

    fun useResourceDirectory(): DotenvBuilder {
        resources = true;
        return this
    }

    fun ingoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    fun build(): Dotenv {
        if (resources && directoryPath != System.getProperty("user.home"))
            throw DotEnvException("useResourceDirectory and withDirectory are mutually exclusive")
        val reader = DotEnvReader(directoryPath, resources, filename, throwIfMalformed, throwIfMissing)
        val env = reader.read()
//        applyEnv(env)
        return DotenvImpl(env)
    }

//    private fun applyEnv(pairs: List<Pair<String, String>>) {
//        val processBuilder = ProcessBuilder()
//        val env = processBuilder.environment()
//        pairs.forEach {
//            println("applying to env ${it.first} ${it.second}")
//            env[it.first] = it.second
//        }
//
//    }
}

private class DotenvImpl(envVars: List<Pair<String,String>>): Dotenv {
    val map = envVars.associateBy({ it.first }, { it.second })

    override fun get(envVar: String): String? = map[envVar] ?: System.getenv(envVar)
}

private class DotEnvReader(
        val directory: String,
        val useResources: Boolean,
        val filename: String = ".env",
        val throwIfMalformed: Boolean,
        val throwIfMissing: Boolean
) {
    private val commentHash = "#"
    private val commentSlashes = """//"""

    fun read() = parse()

    private fun parse(): List<Pair<String, String>> {
        val isWhiteSpace = { s: String -> """^\s*${'$'}""".toRegex().matches(s) }
        val isComment = { s: String -> s.startsWith(commentHash) || s.startsWith(commentSlashes) }
        val parseLine = { s: String -> """^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$""".toRegex().matchEntire(s) }

        val classLoader = javaClass.classLoader
        val userSpecifiedPath =
            if (useResources) File(classLoader.getResource(filename)!!.file).toPath()
            else Paths.get(directory, filename)

        val cwd = if (useResources) Paths.get("./") else Paths.get(System.getProperty("user.dir"))
        val path = cwd.resolve(userSpecifiedPath)

        val lines =
                try { Files.lines(path) }
                catch (e: Exception) {
                    if (throwIfMissing) throw DotEnvException(e)
                    else Stream.empty<String>()
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

