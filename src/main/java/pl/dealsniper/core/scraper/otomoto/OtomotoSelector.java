/* (C) 2025 */
package pl.dealsniper.core.scraper.otomoto;

import pl.dealsniper.core.scraper.Selector;

public final class OtomotoSelector extends Selector {

    private OtomotoSelector() {}

    public static final String OFFER_ID = "article[data-id]";

    public static final String OFFER_TITLE = "article[data-id] h2";

    public static final String OFFER_LOCATION = "article[data-id] ul li p";

    public static final String OFFER_PRICE = "article[data-id] h3";

    public static final String OFFER_CURRENCY = "article[data-id] h3 + p";

    public static final String OFFER_MILEAGE = "dd[data-parameter=mileage]";

    public static final String OFFER_PRODUCTION_YEAR = "dd[data-parameter=year]";
}
