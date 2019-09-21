/**
 * Copyright (c) Carmine DiMascio 2017 - 2019
 * License: MIT
 */
package io.github.cdimascio.dotenv.internal

import io.github.cdimascio.dotenv.DotEnvException
import java.util.stream.Collectors


internal class DotenvParser(
        private val reader: DotenvReader,
        private val throwIfMalformed: Boolean,
        private val throwIfMissing: Boolean
) {
    private val isWhiteSpace = { s: String -> """^\s*${'$'}""".toRegex().matches(s) }
    private val isComment = { s: String -> s.startsWith("#") || s.startsWith("""//""") }
    private val parseLine = { s: String -> """^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$""".toRegex().matchEntire(s) }
    private val isQuoted = { s: String -> s.startsWith("\"") && s.endsWith("\"") }
    /**
     * Parses the contents of .env and returns the environment variable and associated value as a list of pairs
     * @return a list of pair representing the values .env is contributing to the virtual environment
     */
    fun parse(): List<Pair<String, String>> {
        val lines = try {
            reader.read()
        } catch (e: DotEnvException) {
            if (throwIfMissing) throw e
            else return listOf()
        }.collect(Collectors.toList())

        return lines
                .map { it.trim() }
                .filter { !isWhiteSpace(it) }
                .filter { !isComment(it) }
                .mapNotNull {
                    val match = parseLine(it)
                    if (match != null) {
                        val (key, _, value) = match.destructured
                        Pair(key, normalizeValue(value))
                    } else {
                        if (throwIfMalformed) throw DotEnvException("Malformed entry: $it")
                        else null
                    }
                }
    }

    private fun normalizeValue(value: String): String {
        val tr = value.trim()
        return if (isQuoted(tr)) tr.substring(1 until value.length -1) else tr
    }
}
