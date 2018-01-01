package tests;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavaTests {
    @Test(expected = DotEnvException.class)
    public void throwIfMalconfigured() {
        Dotenv.configure().load();
    }

    @Test(expected = DotEnvException.class)
    public void load() {
        Dotenv dotenv = Dotenv.load();
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
    }

    @Test
    public void configurWithIgnoreMalformed() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .load();
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
    }

    @Test
    public void configurWithIgnoreMissingAndMalformed() {
        Dotenv dotenv = Dotenv.configure()
                .directory("/missing/dir")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        assertNotNull(dotenv.get("HOME"));
    }
}
