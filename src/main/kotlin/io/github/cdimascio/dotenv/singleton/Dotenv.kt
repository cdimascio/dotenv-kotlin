package io.github.cdimascio.dotenv.singleton

import io.github.cdimascio.dotenv.DotenvBuilder
import io.github.cdimascio.dotenv.DotenvBuilderImpl
import io.github.cdimascio.dotenv.Dotenv as DotenvInstance

private const val WARN_DOT_ENV_NOT_CONFIGURED = "Warning: dotenv was not configured. Using the default configuration. It is recommended to first configure dotenv. You can do so by calling Dotenv.configure()"
private const val WARN_DOT_ENV_NOT_LOADED = "Warning: dotenv was not loaded. Loading the current configuration. It is recommended to call load() explicitly e.g Dotenv.configure().load()"

object Dotenv {
    private var builder: DotenvBuilderDecorated? = null
    @JvmStatic
    fun configure(): DotenvBuilder {
        val configuredBuilder = DotenvBuilderDecorated(DotenvBuilderImpl())
        builder = configuredBuilder
        return configuredBuilder
    }
    @JvmStatic
    operator fun get(envVar: String): String? {
        val b = builder
        if (b == null) {
            println(WARN_DOT_ENV_NOT_CONFIGURED)
            builder = DotenvBuilderDecorated(DotenvBuilderImpl())
            builder?.load()
        } else if (!b.isLoaded()) {
            println(WARN_DOT_ENV_NOT_LOADED)
            b.load()
        }
        return b?.instance()?.get(envVar)
    }
}

private class DotenvBuilderDecorated(private val b: DotenvBuilder) : DotenvBuilder by b {
    private var instance: DotenvInstance? = null

    override fun load(): DotenvInstance {
        val dotenv = b.load()
        instance = dotenv
        return dotenv
    }

    override fun filename(name: String): DotenvBuilder {
        b.filename(name)
        return this
    }

    override fun directory(path: String): DotenvBuilder {
        b.directory(path)
        return this
    }

    override fun ignoreIfMissing(): DotenvBuilder {
        b.ignoreIfMissing()
        return this
    }

    override fun ignoreIfMalformed(): DotenvBuilder {
        b.ignoreIfMissing()
        return this
    }

    fun isLoaded() = instance != null
    fun instance() = instance
}
