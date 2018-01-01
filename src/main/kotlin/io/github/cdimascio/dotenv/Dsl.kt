package io.github.cdimascio.dotenv

fun dotenv(block: Configuration.() -> Unit = {}): Dotenv {
    val config = Configuration()
    block(config)
    val dotenv = Dotenv.configure().directory(config.directory)
    if (config.ignoreIfMalformed) dotenv.ignoreIfMalformed()
    if (config.ignoreIfMissing) dotenv.ignoreIfMissing()
    return dotenv.load()
}

class Configuration {
    var directory: String = ".env"
    var ignoreIfMalformed = false
    var ignoreIfMissing = false
}