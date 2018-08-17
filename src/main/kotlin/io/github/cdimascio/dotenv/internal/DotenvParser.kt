/**
 * Copyright (c) Carmine DiMascio 2017 - 2018
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
                        Pair(key, value)
                    } else {
                        if (throwIfMalformed) throw DotEnvException("Malformed entry: $it")
                        else null
                    }
                }
    }
}
