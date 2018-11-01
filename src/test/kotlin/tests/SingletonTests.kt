package tests

import io.github.cdimascio.dotenv.DotEnvException
import io.github.cdimascio.dotenv.singleton.dsl.dotenv
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test as test

class DotenvSingletonTest {
    private val envVars = mapOf(
        "MY_TEST_EV1" to "my test ev 1",
        "MY_TEST_EV2" to "my test ev 2"
    )

    @test(expected = DotEnvException::class)
    fun dotenvMalformed() {
        dotenv.configure()
    }

    @test
    fun dotenvIgnoreMalformed() {
        dotenv.configure {
            ignoreIfMalformed = true
        }

        envVars.forEach {
            val expected = it.value
            val actual = dotenv[it.key]
            assertEquals(expected, actual)
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv.get("HOME")
        assertEquals(expectedHome, actualHome)
    }

    @test
    fun resourceRelative() {
        dotenv.configure {
            directory = "./"
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
    }

    @test
    fun resourceFilename() {
        dotenv.configure {
            filename = "env"
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
    }

    @test
    fun resourceCurrent() {
        dotenv.configure {
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
    }

    @test(expected = DotEnvException::class)
    fun dotenvMissing() {
        dotenv.configure {
            directory = "/missing/.env"
        }
    }

    @test
    fun dotenvIgnoreMissing() {
        dotenv.configure {
            directory = "/missing/.env"
            ignoreIfMissing = true
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
        assertNull(dotenv["MY_TEST_EV1"])
    }
}