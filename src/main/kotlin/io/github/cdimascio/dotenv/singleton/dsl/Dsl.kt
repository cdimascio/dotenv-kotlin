package io.github.cdimascio.dotenv.singleton.dsl

import io.github.cdimascio.dotenv.Configuration
import io.github.cdimascio.dotenv.Dotenv as DotenvInstance
import io.github.cdimascio.dotenv.singleton.Dotenv as DotenvSingleton

private const val WARN_DOT_ENV_NOT_CONFIGURED = "Warning: dotenv was not configured. Using the default configuration. It is recommended to first configure dotenv by calling dotenv.configure(...)"
private var instance: DotenvInstance? = null

object dotenv {
    fun configure(block: Configuration.() -> Unit = {}) {
        val config = Configuration()
        block(config)
        val dotenv = DotenvSingleton.configure()
        dotenv.directory(config.directory)
        dotenv.filename(config.filename)
        if (config.ignoreIfMalformed) dotenv.ignoreIfMalformed()
        if (config.ignoreIfMissing) dotenv.ignoreIfMissing()
        val i = dotenv.load()
        instance = i
    }

    operator fun get(envVar: String): String? {
        if (instance == null) {
            println(WARN_DOT_ENV_NOT_CONFIGURED)
            DotenvSingleton.configure().load()
        }
        return DotenvSingleton[envVar]
    }
}
