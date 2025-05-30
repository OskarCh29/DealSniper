package pl.dealniper.core.scraper.otomoto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.dealniper.core.exception.UrlTimeoutException;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.model.CarDeal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OtomotoScraper implements Scraper<CarDeal> {

    private static final int LOCATION_WITH_STATE = 2;
    private static final int LOCATION = 0;
    private static final int LOCATION_STATE = 1;

    @Override
    public boolean supports(String platformUrl) {
        return platformUrl.toLowerCase().contains("otomoto");
    }

    @Override
    public List<CarDeal> getDeals(String platformUrl) {
        List<CarDeal> carDeals = new ArrayList<>();

        try {
            Elements listings = getUrlElements(platformUrl,OtomotoSelector.USER_AGENT,OtomotoSelector.LANGUAGE_HEADER,
                    OtomotoSelector.REQUEST_TIMEOUT, OtomotoSelector.OFFER_ID);

            for (Element element : listings) {
                if (carDeals.size() >= OtomotoSelector.MAX_OFFER_RESULT) {
                    break;
                }
                String title = element.select(OtomotoSelector.OFFER_TITLE).text();

                String url = element.select(OtomotoSelector.OFFER_LINK).attr(OtomotoSelector.OFFER_URL);

                String locationTag = element.select(OtomotoSelector.OFFER_LOCATION).text();
                String[] words = locationTag.split(" ");
                String location = locationTag.length() >= LOCATION_WITH_STATE ?
                        words[LOCATION] + " " + words[LOCATION_STATE] : locationTag;

                String priceStr = element.select(OtomotoSelector.OFFER_PRICE).text().replace(" ", "");
                BigDecimal price = new BigDecimal(priceStr.isEmpty() ? "0" : priceStr);

                String mileage = element.select(OtomotoSelector.OFFER_MILEAGE).text();

                String productionYear = element.select(OtomotoSelector.OFFER_PRODUCTION_YEAR).text();
                int year = Integer.parseInt(productionYear);

                carDeals.add(new CarDeal(title, price, url, location, mileage, year));
            }
        } catch (IOException e) {
            throw new UrlTimeoutException("Request timeout exceeded");
        }

        return carDeals;
    }
}
