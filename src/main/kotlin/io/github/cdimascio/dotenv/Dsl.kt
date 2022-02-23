/**
 * Copyright (c) Carmine DiMascio 2017 - 2021
 * License: MIT
 */
package io.github.cdimascio.dotenv

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Configure dotenv
 */
@OptIn(ExperimentalContracts::class)
fun dotenv(block: Configuration.() -> Unit = {}): Dotenv {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val config = Configuration()
    block(config)
    val dotenv = Dotenv.configure()
    dotenv.directory(config.directory)
    dotenv.filename(config.filename)
    if (config.ignoreIfMalformed) dotenv.ignoreIfMalformed()
    if (config.ignoreIfMissing) dotenv.ignoreIfMissing()
    if (config.systemProperties) dotenv.systemProperties()
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

    /**
     * Set env vars into System properties. Enables fetch them via e.g. System.getProperty(...)
     */
    var systemProperties = false
}
