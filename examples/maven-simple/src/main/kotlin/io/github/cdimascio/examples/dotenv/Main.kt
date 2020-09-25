package io.github.cdimascio.examples.dotenv

import io.github.cdimascio.dotenv.Dotenv

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val dotenv = Dotenv.configure().load()

        // Iterate over each environment entry
        // Note: entries in the host environment override entries in .env
        dotenv.entries().forEach { println(it) }

        // Retrieve the value of the MY_ENV environment variable
        println("MY_ENV: " + dotenv["MY_ENV"])

        // Retrieve the value of the MY_ENV2 environment variable or return a default value
        println("MY_ENV2: " + dotenv["MY_ENV2", "Default Value"])
    }
}
