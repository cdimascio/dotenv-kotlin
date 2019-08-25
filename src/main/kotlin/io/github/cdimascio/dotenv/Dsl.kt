/**
 * Copyright (c) Carmine DiMascio 2017 - 2019
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
    /**
     * Set the directory containing the .env file
     */
    var directory: String = "./"
    /**
     * Sets the name of the .env. The default is .env
     */
    var filename: String = ".env"
    /**
     * Do not throw an exception when .env is malformed
     */
    var ignoreIfMalformed = false
    /**
     * Do not throw an exception when .env is missing
     */
    var ignoreIfMissing = false
}
