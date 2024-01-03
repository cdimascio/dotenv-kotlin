package tests;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JavaTests {
    private final Map<String, String> envVars = Map.of(
        "MY_TEST_EV1", "my test ev 1",
        "MY_TEST_EV2", "my test ev 2",
        "WITHOUT_VALUE", ""
    );

    @Test
    public void throwIfMalconfigured() {
        assertThrows(DotenvException.class, () -> Dotenv.configure().load());
    }

    @Test
    public void load() {
        assertThrows(DotenvException.class, () -> {
            Dotenv dotenv = Dotenv.load();
            envVars.keySet().forEach(envName -> assertEquals(envVars.get(envName), dotenv.get(envName)));
        });
    }

    @Test
    public void iteratorOverDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        dotenv.entries().forEach(e -> assertEquals(dotenv.get(e.getKey()), e.getValue()));
    }

    @Test
    public void iteratorOverDotenvWithFilter() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        Set<DotenvEntry> entriesInFile = dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE);
        Set<DotenvEntry> entriesAll = dotenv.entries();
        assertTrue(entriesInFile.size() < entriesAll.size());

        envVars.forEach((key, value) -> assertEquals(dotenv.get(key), value));
    }

    @Test
    public void failToRemoveFromDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        assertThrows(UnsupportedOperationException.class, () -> {
            Iterator<DotenvEntry> iter = dotenv.entries().iterator();
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
            }
        });
    }

    @Test
    public void failToAddToDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        assertThrows(UnsupportedOperationException.class, () -> dotenv.entries().add(new DotenvEntry("new", "value")));
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
