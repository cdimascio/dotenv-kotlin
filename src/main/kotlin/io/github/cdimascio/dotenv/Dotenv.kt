/**
 * Copyright (c) Carmine DiMascio 2017 - 2019
 * License: MIT
 */
package io.github.cdimascio.dotenv

import io.github.cdimascio.dotenv.internal.DotenvParser
import io.github.cdimascio.dotenv.internal.DotenvReader
import java.util.Collections

/**
 * Dotenv
 * @see <a href="https://github.com/cdimascio/java-dotenv">The complete dotenv documentation</a>
 */
abstract class Dotenv {
    /**
     * The dotenv instance
     */
    companion object Instance {
        /**
         * Configure dotenv
         * @return A dotenv builder
         */
        @JvmStatic
        fun configure(): DotenvBuilder = DotenvBuilder()

        /**
         * Load the the contents of .env into the virtual environment.
         * Environment variables in the host environment override those in .env
         */
        @JvmStatic
        fun load(): Dotenv = DotenvBuilder().load()
    }

    /**
     * Returns the value for the specified environment variable
     *
     * @param envName The environment variable name
     */
    abstract operator fun get(envName: String): String?

    /**
     * Returns all environment entries
     */
    abstract fun entries(): Set<DotenvEntry>

    /**
     * Returns all environment entries filtered by {@link DotenvEntriesFilter}
     */
    abstract fun entries(filter: DotenvEntriesFilter): Set<DotenvEntry>

    /**
     * Returns the value for the environment variable, or the default value if absent
     *
     * @param envName The environment variable name
     * @param defValue The default value
     */
    fun get(envName: String, defValue: String): String {
        return get(envName) ?: defValue
    }
}

/**
 * Dotenv exception
 */
class DotEnvException(message: String) : Exception(message)

/**
 * Constructs a new DotenvBuilder
 */
class DotenvBuilder internal constructor() {
    private var filename = ".env"
    private var directoryPath = "./"
    private var systemProperties = false
    private var throwIfMissing = true
    private var throwIfMalformed = true


    /**
     * Sets the directory containing the .env file
     * @param path The path
     */
    fun directory(path: String = directoryPath): DotenvBuilder {
        directoryPath = path
        return this
    }

    /**
     * Sets the name of the .env file. The default is .env
     * @param name The filename
     */
    fun filename(name: String = ".env"): DotenvBuilder {
        filename = name
        return this
    }

    /**
     * Do not throw an exception when .env is missing
     */
    fun ignoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    /**
     * Do not throw an exception when .env is malformed
     */
    fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    /**
     * Adds environment variables into system properties
     */
    fun systemProperties(): DotenvBuilder {
        systemProperties = true
        return this
    }

    /**
     * Load the contents of .env into the virtual environment
     */
    fun load(): Dotenv {
        val reader = DotenvParser(
            DotenvReader(directoryPath, filename),
            throwIfMalformed,
            throwIfMissing)
        val env = reader.parse()
        if (systemProperties) {
            env.forEach { System.setProperty(it.first, it.second) }
        }
        return DotenvImpl(env)
    }
}
enum class DotenvEntriesFilter {
    DECLARED_IN_ENV_FILE
}
/**
 * A dotenv entry containing a key, value pair
 */
data class DotenvEntry(val key: String, val value: String)

private class DotenvImpl(envVars: List<Pair<String, String>>) : Dotenv() {
    private val map = envVars.associateBy({ it.first }, { it.second })
    private val set: Set<DotenvEntry> = Collections.unmodifiableSet(
        buildEnvEntries().map { DotenvEntry(it.key, it.value) }.toSet()
    )

    override fun entries() = set
    override fun entries(filter: DotenvEntriesFilter) = map.map { DotenvEntry(it.key, it.value) }.toSet()
    override fun get(envName: String): String? = System.getenv(envName) ?: map[envName]

    private fun buildEnvEntries(): Map<String, String> {
        val envMap = map.toMap(mutableMapOf())
        System.getenv().entries.forEach {
            envMap[it.key] = it.value
        }
        return envMap
    }
}
