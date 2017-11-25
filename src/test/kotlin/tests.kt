package tests

import io.cdimascio.DotEnvException
import io.cdimascio.Dotenv
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
                .withDirectory("./src/test/resources")
                .build()
    }

    @test fun dotenvIgnoreMalformed() {
        val dotEnv = Dotenv
                .configure()
                .withDirectory("./src/test/resources")
                .ignoreIfMalformed()
                .build()


        envVars.forEach {
            val expected = it.value
            val actual = dotEnv.get(it.key)
            assertEquals(expected, actual)
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotEnv.get("HOME")
        assertEquals(expectedHome, actualHome)

    }

    @test(expected = DotEnvException::class) fun dotenvMissing() {
        Dotenv.configure()
                .withDirectory("/missing/.env")
                .build()
    }

    @test fun dotenvIgnoreMissing() {
        Dotenv.configure()
                .withDirectory("/missing/.env")
                .ingoreIfMissing()
                .build()
    }

    @test fun dotenvAbsolutePath() {
        Dotenv.configure()
                .withDirectory("/missing/.env")
                .ingoreIfMissing()
                .build()
    }
}