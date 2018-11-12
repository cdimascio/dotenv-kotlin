/**
 * Copyright (c) Carmine DiMascio 2017 - 2018
 * License: MIT
 */
package io.github.cdimascio.dotenv

/**
 * Configure dotenv
 */
fun dotenv(block: Configuration.() -> Unit = {}): Dotenv {
    val config = Configuration()
    block(config)
    val dotenv = Dotenv.configure()
    dotenv.directory(config.directory)
    dotenv.filename(config.filename)
    if (config.ignoreIfMalformed) dotenv.ignoreIfMalformed()
    if (config.ignoreIfMissing) dotenv.ignoreIfMissing()
    return dotenv.load()
}

/**
 * The dotenv configuration
 */
class Configuration {
    var directory: String = ""
    var filename: String = ".env"
    var ignoreIfMalformed = false
    var ignoreIfMissing = false
}
