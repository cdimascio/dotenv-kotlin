/**
 * Copyright (c) Carmine DiMascio 2017 - 2018
 * License: MIT
 */
package io.github.cdimascio.dotenv

import io.github.cdimascio.dotenv.internal.DotenvParser
import io.github.cdimascio.dotenv.internal.DotenvReader

abstract class Dotenv {
    companion object Instance {
        @JvmStatic fun configure(): DotenvBuilder = DotenvBuilder()
        @JvmStatic fun load(): Dotenv = DotenvBuilder().load()
    }

    operator abstract fun get(envVar: String): String?
}

class DotEnvException(message: String) : Exception(message)

class DotenvBuilder internal constructor() {
    private var filename = ".env"
    private var directoryPath = ""
    private var throwIfMissing = true
    private var throwIfMalformed = true

    fun directory(path: String = directoryPath): DotenvBuilder {
        directoryPath = path
        return this
    }

    fun filename(name: String = ".env"): DotenvBuilder {
        filename = name
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

    fun load(): Dotenv {
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