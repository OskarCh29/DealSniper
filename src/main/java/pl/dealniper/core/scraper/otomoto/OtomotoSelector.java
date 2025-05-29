package pl.dealniper.core.scraper.otomoto;

import pl.dealniper.core.scraper.Selector;

public final class OtomotoSelector extends Selector {

    private OtomotoSelector() {

    }

    public static final String OFFER_ID = "article[data-id]";

    public static final String OFFER_TITLE = "h2";

    public static final String OFFER_LOCATION = "p[class*=ooa-oj1jk2]";

    public static final String OFFER_PRICE = "h3[class*=ooa-1n2paoq]";

    public static final String OFFER_MILEAGE = "dd[data-parameter=mileage]";

    public static final String OFFER_PRODUCTION_YEAR = "dd[data-parameter=year]";
}
