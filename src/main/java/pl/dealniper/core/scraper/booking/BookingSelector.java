package pl.dealniper.core.scraper.booking;

import pl.dealniper.core.scraper.Selector;

public final class BookingSelector extends Selector {

    private BookingSelector() {

    }

    public static final String OFFER_ID = "div[data-testid=property-card]";

    public static final String OFFER_TITLE = "div[data-testid=title]";

    public static final String OFFER_ROOM_TYPE = "div[data-testid=property-card] h4";

    public static final String OFFER_PRICE = "span[data-testid=price-and-discounted-price]";

    public static final String OFFER_SCORE = "div[data-testid=review-score] > div[aria-hidden=true]";

}
