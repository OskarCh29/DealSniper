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
    private static final int LOCATION = 1;
    private static final int LOCATION_STATE = 2;

    @Override
    public boolean supports(String platformUrl) {
        return "otomoto".equalsIgnoreCase(platformUrl);
    }

    @Override
    public List<CarDeal> getDeals(String platformUrl) {
        List<CarDeal> carDeals = new ArrayList<>();
        int servicePage = 1;

        while (servicePage <= OtomotoSelector.MAX_OFFER_PAGE) {
            try {
                String paginatedUrl = addPageParam(platformUrl, servicePage);
                Document doc = Jsoup.connect(paginatedUrl)
                        .userAgent(OtomotoSelector.USER_AGENT)
                        .header("Accept-Language", OtomotoSelector.LANGUAGE_HEADER)
                        .header("Connection", "keep-alive")
                        .timeout(OtomotoSelector.REQUEST_TIMEOUT)
                        .get();
                Elements listings = doc.select(OtomotoSelector.OFFER_ID);

                if (listings.isEmpty()) {
                    break;
                }

                for (Element element : listings) {
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

                    carDeals.add(new CarDeal(title, price, location, mileage, year, url));
                }
                servicePage++;
            } catch (IOException e) {
                throw new UrlTimeoutException("Request timeout exceeded");
            }
        }
        return carDeals;
    }

    private String addPageParam(String url, int page) {
        if (url.contains("?")) {
            return url + "&page=" + page;
        } else {
            return url + "?page=" + page;
        }
    }
}
