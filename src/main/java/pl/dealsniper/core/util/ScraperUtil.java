/* (C) 2025 */
package pl.dealsniper.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScraperUtil {

    private ScraperUtil() {
    }

    public static final String LANGUAGE_HEADER = "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7";

    public static final int REQUEST_TIMEOUT = 30000;

    public static final int MAX_OFFER_RESULT = 100;

    public static final String OFFER_URL = "a";

    private static final List<String> USER_AGENTS = loadUserAgents();

    private static final List<String> REFERERS = List.of(
            "https://www.google.com/",
            "https://www.bing.com/",
            "https://www.yahoo.com/",
            "https://www.duckduckgo.com/");


    public static String getRandomUserAgent() {
        return USER_AGENTS.get(ThreadLocalRandom.current().nextInt(USER_AGENTS.size()));
    }

    public static String getRandomReferer() {
        return REFERERS.get(ThreadLocalRandom.current().nextInt(REFERERS.size()));
    }

    private static List<String> loadUserAgents() {
        try (InputStream inputStream = ScraperUtil.class.getClassLoader().getResourceAsStream("config/user_agents.txt")) {
            if (inputStream == null) {
                throw new IllegalStateException("user_agents.txt configuration file was not found");
            }
            List<String> agents = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .filter(line -> !line.isBlank())
                    .toList();
            ValidationUtil.throwIfTrue(agents.isEmpty(),
                    () -> new IllegalStateException("user_agents.txt is empty - at least one user-agent is required"));

            return agents;

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load user_agents.txt");
        }
    }
}
