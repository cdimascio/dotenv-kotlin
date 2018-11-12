package tests

import io.github.cdimascio.dotenv.DotEnvException
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test as test

class DotEnvDslTest {
    private val envVars = mapOf(
            "MY_TEST_EV1" to "my test ev 1",
            "MY_TEST_EV2" to "my test ev 2"
    )

    @test(expected = DotEnvException::class)
    fun dotenvMalformed() {
        dotenv()
    }

    @test
    fun dotenvIgnoreMalformed() {
        val env = dotenv {
            ignoreIfMalformed = true
        }

        envVars.forEach {
            val expected = it.value
            val actual = env[it.key]
            assertEquals(expected, actual)
        }

        assertHostEnvVar(env)
    }

    @test
    fun resourceRelative() {
        val env = dotenv {
            directory = "./"
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", env["MY_TEST_EV1"])

        assertHostEnvVar(env)
    }

    @test
    fun resourceFilename() {
        val env = dotenv {
            filename = "env"
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", env["MY_TEST_EV1"])

        assertHostEnvVar(env)
    }

    @test
    fun resourceCurrent() {
        val env = dotenv {
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", env["MY_TEST_EV1"])

        assertHostEnvVar(env)
    }

    @test(expected = DotEnvException::class)
    fun dotenvMissing() {
        dotenv {
            directory = "/missing/.env"
        }
    }

    @test
    fun dotenvIgnoreMissing() {
        val env = dotenv {
            directory = "/missing/.env"
            ignoreIfMissing = true
        }

        assertHostEnvVar(env)
        assertNull(env["MY_TEST_EV1"])
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
