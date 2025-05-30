package pl.dealniper.core.scraper.olx;

import org.jsoup.nodes.Element;
import pl.dealniper.core.scraper.Selector;

public class OlxSelector extends Selector {

    public static final String OFFER_ID = "div[data-cy=l-card]";

    public static final String OFFER_TITLE = "div[data-cy=ad-card-title] h4";

    public static final String OFFER_PRICE = "p[data-testid=ad-price]";

    public static final String OFFER_LOCATION = "p[data-testid=location-date]";

    public static final String OFFER_LINK = "div[data-cy=ad-card-title] a";

    public static final String BASE_URL = "https://www.olx.pl";

    public static final String PAID_OFFER = "div[class*=css-qavd0c] > div[class*=css-s3yjnp]";

    private OlxSelector() {

    }

    public static boolean isOfferPaid(Element element) {
        return !element.select(PAID_OFFER).isEmpty();
    }

}
