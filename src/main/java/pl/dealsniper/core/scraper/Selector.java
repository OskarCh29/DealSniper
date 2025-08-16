package pl.dealsniper.core.scraper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public abstract class Selector {

    public static final String LANGUAGE_HEADER = "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7";

    public static final int REQUEST_TIMEOUT = 10000;

    public static final int MAX_OFFER_RESULT = 100;

    public static final String OFFER_URL = "a";

    private static final List<String> USER_AGENTS = loadUserAgents();

    private static final List<String> REFERERS = List.of(
            "https://www.google.com/",
            "https://www.bing.com/",
            "https://www.yahoo.com/",
            "https://www.duckduckgo.com/");
    private static final Random RANDOM = new Random();

    public static String getRandomUserAgent() {
        if (USER_AGENTS.isEmpty()) {
            throw new IllegalStateException("No agents found in config file");
        }
        return USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size()));
    }

    public static String getRandomReferer() {
        if (REFERERS.isEmpty()) {
            throw new IllegalStateException("No referers found in config file");
        }
        return REFERERS.get(RANDOM.nextInt(REFERERS.size()));
    }

    private static List<String> loadUserAgents() {
        try {
            return Files.readAllLines(Paths.get("config/user_agents.txt"));

        } catch (IOException e) {
            throw new IllegalStateException("File user agents failed when loading");
        }
    }
}
