package tests

import io.github.cdimascio.DotEnvException
import io.github.cdimascio.Dotenv
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test as test

class DotEnvTest() {
    val envVars = mapOf(
            "MY_TEST_EV1" to "my test ev 1",
            "MY_TEST_EV2" to "my test ev 1"
    )

    @test(expected = DotEnvException::class) fun dotenvMalformed() {
        Dotenv
                .configure()
                .useDirectory("./src/test/resources")
                .build()
    }

    @test fun dotenvIgnoreMalformed() {
        val dotEnv = Dotenv
                .configure()
                .useDirectory("./src/test/resources")
                .ignoreIfMalformed()
                .build()


        envVars.forEach {
            val expected = it.value
            val actual = dotEnv[it.key]
            assertEquals(expected, actual)
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotEnv.get("HOME")
        assertEquals(expectedHome, actualHome)
    }

    @test fun dotenvUseResources() {
        val dotEnv = Dotenv
                .configure()
                .useDirectory("./src/test/resources")
                .ignoreIfMalformed()
                .build()


        envVars.forEach {
            val expected = it.value
            val actual = dotEnv[it.key]
            assertEquals(expected, actual)
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotEnv.get("HOME")
        assertEquals(expectedHome, actualHome)

    }

    @test(expected = DotEnvException::class) fun useDirectoryMutuallyExclusive() {
        Dotenv.configure()
                .useDirectory("/missing/.env")
                .useResourceDirectory()
                .build()
    }

    @test(expected = DotEnvException::class) fun dotenvMissing() {
        Dotenv.configure()
                .useDirectory("/missing/.env")
                .build()
    }

    @test fun dotenvIgnoreMissing() {
        Dotenv.configure()
                .useDirectory("/missing/.env")
                .ingoreIfMissing()
                .build()
    }

    @test fun dotenvAbsolutePath() {
        Dotenv.configure()
                .useDirectory("/missing/.env")
                .ingoreIfMissing()
                .build()
    }
}