package tests

import io.github.cdimascio.dotenv.DotenvException
import io.github.cdimascio.dotenv.Dotenv
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test as test

class DotEnvTest {
    private val envVars = mapOf(
        "MY_TEST_EV1" to "my test ev 1",
        "MY_TEST_EV2" to "my test ev 2",
        "WITHOUT_VALUE" to "",
        "MULTI_LINE" to "hello\\nworld"
    )

    @test(expected = DotenvException::class)
    fun dotenvMalformed() {
        Dotenv.configure()
            .directory("./src/test/resources")
            .load()
    }

    @test
    fun dotenvIgnoreMalformed() {
        val dotenv = Dotenv.configure().apply {
            directory("./src/test/resources")
            ignoreIfMalformed()
        }.load()

        envVars.forEach {
            val expected = it.value
            val actual = dotenv[it.key]
            assertEquals(expected, actual)
        }

        assertHostEnvVar(dotenv)
    }

    @test
    fun dotenvFilename() {
        val dotenv = Dotenv.configure().apply {
            directory("./src/test/resources")
            filename("env")
            ignoreIfMalformed()
        }.load()

        envVars.forEach {
            val expected = it.value
            val actual = dotenv[it.key]
            assertEquals(expected, actual)
        }

        assertHostEnvVar(dotenv)
    }

    @test
    fun resourceRelative() {
        val dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMalformed()
            .load()
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        assertHostEnvVar(dotenv)
    }

    @test
    fun resourceCurrent() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load()
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        assertHostEnvVar(dotenv)
    }

    @test
    fun systemProperties() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .systemProperties()
            .load()

        assertHostEnvVar(dotenv)
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])
        assertEquals("my test ev 1", System.getProperty("MY_TEST_EV1"))
        dotenv.entries().forEach {
            System.clearProperty(it.key)
        }
    }

    @test
    fun noSystemProperties() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load()

        assertHostEnvVar(dotenv)
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])
        assertNull(System.getProperty("MY_TEST_EV1"))
    }

    @test
    fun iterateOverDotenv() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load()

        for (e in dotenv.entries()) {
            assertEquals(dotenv[e.key], e.value)
        }
    }

    @test(expected = DotenvException::class)
    fun dotenvMissing() {
        Dotenv.configure()
            .directory("/missing/.env")
            .load()
    }

    @test
    fun dotenvIgnoreMissing() {
        val dotenv = Dotenv.configure()
            .directory("/missing/.env")
            .ignoreIfMissing()
            .load()

        assertHostEnvVar(dotenv)

        assertNull(dotenv["MY_TEST_EV1"])
    }

    private fun assertHostEnvVar(env: Dotenv) {
        val isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0
        if (isWindows) {
            val path = env["PATH"]
            assertNotNull(path)
        } else {
            val expectedHome = System.getProperty("user.home")
            val actualHome = env["HOME"]
            assertEquals(expectedHome, actualHome)
        }
    }
}
