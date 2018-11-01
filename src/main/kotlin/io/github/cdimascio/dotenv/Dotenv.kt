/**
 * Copyright (c) Carmine DiMascio 2017 - 2018
 * License: MIT
 */
package io.github.cdimascio.dotenv

import io.github.cdimascio.dotenv.internal.DotenvParser
import io.github.cdimascio.dotenv.internal.DotenvReader

interface DotenvBuilder {
    fun filename(name: String = ".env"): DotenvBuilder
    fun directory(path: String = ""): DotenvBuilder
    fun ignoreIfMissing(): DotenvBuilder
    fun ignoreIfMalformed(): DotenvBuilder
    fun load(): Dotenv
}

abstract class Dotenv {
    companion object Instance {
        @JvmStatic fun configure(): DotenvBuilder = DotenvBuilderImpl()
        @JvmStatic fun load(): Dotenv = DotenvBuilderImpl().load()
    }

    operator abstract fun get(envVar: String): String?
}

class DotEnvException(message: String) : Exception(message)

class DotenvBuilderImpl internal constructor(): DotenvBuilder {
    private var filename = ".env"
    private var directoryPath = ""
    private var throwIfMissing = true
    private var throwIfMalformed = true

    override fun directory(path: String): DotenvBuilder {
        directoryPath = path
        return this
    }

    override fun filename(name: String): DotenvBuilder {
        filename = name
        return this
    }

    override fun ignoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    override fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    override fun load(): Dotenv {
        val reader = DotenvParser(
                DotenvReader(directoryPath, filename),
                throwIfMalformed,
                throwIfMissing)
        val env = reader.parse()
        return DotenvImpl(env)
    }
}

private class DotenvImpl(envVars: List<Pair<String, String>>) : Dotenv() {
    private val map = envVars.associateBy({ it.first }, { it.second })

    override fun get(envVar: String): String? = System.getenv(envVar) ?: map[envVar]
}