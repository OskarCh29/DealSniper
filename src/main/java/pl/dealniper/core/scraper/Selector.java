package pl.dealniper.core.scraper;

public abstract class Selector {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 11.0; Win64; x64)"
            + " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.6998.166 Safari/537.36";

    public static final String LANGUAGE_HEADER = "pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7";

    public static final int REQUEST_TIMEOUT = 10000;

    public static final int MAX_OFFER_PAGE = 3;

    public static final String OFFER_LINK = "a";

    public static final String OFFER_URL = "href";
}
