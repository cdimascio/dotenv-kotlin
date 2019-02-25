package tests;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavaTests {
    private Map<String, String> envVars;

    @Before
    public void setUp() {
        envVars = new HashMap<String, String>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("ABSENT_ENV_VARIABLE", null);
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test(expected = DotEnvException.class)
    public void throwIfMalconfigured() {
        Dotenv.configure().load();
    }

    @Test(expected = DotEnvException.class)
    public void load() {
        Dotenv dotenv = Dotenv.load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMissingAndMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .directory("/missing/dir")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

        assertNotNull(dotenv.get("PATH"));
    }
}
