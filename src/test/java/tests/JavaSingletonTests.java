package tests;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.singleton.Dotenv;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JavaSingletonTests {
    private Map<String, String> envVars = new HashMap<String, String>() {{
        put("MY_TEST_EV1", "my test ev 1");
        put("MY_TEST_EV2", "my test ev 2");
    }};

    @Test(expected = DotEnvException.class)
    public void dotenvMalformed() {
        Dotenv.configure().load();
    }

    @Test
    public void dotenvIgnoreMalformed() {
        Dotenv.configure().ignoreIfMalformed().load();

        envVars.forEach((key, expected) -> {
            // TODO fix api so you can only get but not configure - is this possible?
            String actual = Dotenv.get(key);
            assertEquals(expected, actual);
        });

        String expectedHome = System.getProperty("user.home");
        String actualHome = Dotenv.get("HOME");
        assertEquals(expectedHome, actualHome);
    }

    @Test
    public void resourceRelative() {
        Dotenv.configure()
            .directory("./")
            .ignoreIfMalformed()
            .load();

        assertEquals("my test ev 1", Dotenv.get("MY_TEST_EV1"));

        String expectedHome = System.getProperty("user.home");
        String actualHome = Dotenv.get("HOME");
        assertEquals(expectedHome, actualHome);
    }

    @Test
    public void resourceFilename() {
        Dotenv.configure()
            .filename("env")
            .ignoreIfMalformed()
            .load();

        assertEquals("my test ev 1", Dotenv.get("MY_TEST_EV1"));

        String expectedHome = System.getProperty("user.home");
        String actualHome = Dotenv.get("HOME");
        assertEquals(expectedHome, actualHome);
    }

    @Test
    public void resourceCurrent() {
        Dotenv
            .configure()
            .ignoreIfMalformed()
            .load();

        assertEquals("my test ev 1", Dotenv.get("MY_TEST_EV1"));

        String expectedHome = System.getProperty("user.home");
        String actualHome = Dotenv.get("HOME");
        assertEquals(expectedHome, actualHome);
    }

    @Test(expected = DotEnvException.class)
    public void dotenvMissing() {
        Dotenv
            .configure()
            .directory("/missing/.env")
            .load();
    }

    @Test
    public void dotenvIgnoreMissing() {
        Dotenv.configure()
            .directory("/missing/.env")
            .ignoreIfMissing()
            .load();

        String expectedHome = System.getProperty("user.home");
        String actualHome = Dotenv.get("HOME");
        assertEquals(expectedHome, actualHome);
        Assert.assertNotNull(Dotenv.get("MY_TEST_EV1"));
    }
}
