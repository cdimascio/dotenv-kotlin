package tests

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import io.github.cdimascio.dotenv.dotenv
import kotlin.test.*
import org.junit.jupiter.api.Test as test

class DotEnvDslTest {
    private val envVars = mapOf(
        "MY_TEST_EV1" to "my test ev 1",
        "MY_TEST_EV2" to "my test ev 2",
        "WITHOUT_VALUE" to "",
        "QUOTED_EV1" to "jdbc:hive2://[domain]:10000/default;principal=hive/_HOST@[REALM]",
        "MULTI_LINE" to "hello\\nworld"
    )

    private val envVarsOverridenByHostEnv = mapOf(
        "HOME" to "dotenv_test_home"
    )

    @test
    fun dotenvMalformed() {
        assertFailsWith<DotenvException> {
            dotenv()
        }
    }

    @test
    fun dotenvQuotedEv() {
        val env = dotenv {
            ignoreIfMalformed = true
        }

        envVars.forEach {
            val expected = it.value
            val actual = env[it.key]
            assertEquals(expected, actual)
        }

        assertEquals("jdbc:hive2://[domain]:10000/default;principal=hive/_HOST@[REALM]", env["QUOTED_EV1"])
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
    fun iterateOverDotenv() {
        val env = dotenv {
            filename = "env"
            ignoreIfMalformed = true
        }

        for (e in env.entries()) {
            assertEquals(env[e.key], e.value)
        }
        assertHostEnvVar(env)
        assertNotEquals(envVarsOverridenByHostEnv["HOME"], env["HOME"])
    }

    @test
    fun iteratorOverDotenvWithFilter() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load()

        val entriesInFile = dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE)
        val entriesAll = dotenv.entries()
        assertTrue(entriesInFile.size < entriesAll.size)

        for ((key, value) in envVars) {
            assertEquals(dotenv[key], value)
        }
    }

    @test
    fun multiLine() {
        val dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load()

        assertEquals(dotenv["MULTI_LINE"]!!, envVars["MULTI_LINE"]!!)
    }

    @test
    fun resourceCurrent() {
        val env = dotenv {
            ignoreIfMalformed = true
        }
        assertEquals("my test ev 1", env["MY_TEST_EV1"])

        assertHostEnvVar(env)
    }

    @test
    fun dotenvMissing() {
        assertFailsWith<DotenvException> {
            dotenv {
                directory = "/missing/.env"
            }
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
        val isWindows = System.getProperty("os.name").lowercase().indexOf("win") >= 0
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
