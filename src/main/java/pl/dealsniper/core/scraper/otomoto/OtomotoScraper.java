package pl.dealsniper.core.scraper.otomoto;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dealsniper.core.exception.UrlTimeoutException;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.scraper.Scraper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class OtomotoScraper implements Scraper<CarDeal> {

    private static final int OFFERS_PER_PAGE = 32;
    private static final int MINIMUM_MS_DELAY = 1000;
    private static final int MAX_MS_DELAY = 60000;

    @Override
    public List<CarDeal> getDeals(String platformUrl) {
        List<CarDeal> carDeals = new ArrayList<>();

        int page = 1;
        boolean morePages = true;

        log.info("Start scraping: {}", platformUrl);

        try {
            while (morePages && carDeals.size() < OtomotoSelector.MAX_OFFER_RESULT) {
                String pageUrl = buildPageUrl(platformUrl, page);

                Elements listings = generateUrlRequest(pageUrl, OtomotoSelector.OFFER_ID);

                log.info("Found {} offers on {} page", listings.size(), page);

                if (listings.isEmpty()) {
                    break;
                }

                for (Element element : listings) {
                    if (carDeals.size() >= OtomotoSelector.MAX_OFFER_RESULT) {
                        morePages = false;
                        log.info("Offers limit has been reached: {}", carDeals.size());
                        break;
                    }
                    CarDeal deal = parseElementToCarDeal(element);

                    carDeals.add(deal);

                }
                if (listings.size() == OFFERS_PER_PAGE) {
                    getRandomDelay();
                    page++;
                } else {
                    morePages = false;
                }
            }
        } catch (
                IOException e) {
            log.error("Request time exceeded: {}", e.getMessage());
            throw new UrlTimeoutException("Request timeout exceeded");
        }

        return carDeals;
    }

    private String buildPageUrl(String baseUrl, int page) {
        return UriComponentsBuilder
                .fromUri(URI.create(baseUrl))
                .queryParam("page", page)
                .build()
                .toUriString();
    }

    private CarDeal parseElementToCarDeal(Element element) {
        String title = element.select(OtomotoSelector.OFFER_TITLE).text();

        String textPrice = element.select(OtomotoSelector.OFFER_PRICE).text().replace(" ", "");
        BigDecimal price = new BigDecimal(textPrice);

        String offerCurrency = element.select(OtomotoSelector.OFFER_CURRENCY).text();

        String offerUrl = element.select(OtomotoSelector.OFFER_URL).attr("href");

        String fullLocation = element.select(OtomotoSelector.OFFER_LOCATION).text();
        int locationEndingIndex = fullLocation.indexOf(")");
        String location = fullLocation.substring(0, locationEndingIndex + 1);

        String mileage = element.select(OtomotoSelector.OFFER_MILEAGE).text();

        Integer year = Integer.parseInt(element.select(OtomotoSelector.OFFER_PRODUCTION_YEAR).text());

        return CarDeal.builder()
                .title(title)
                .price(price)
                .currency(offerCurrency)
                .offerUrl(offerUrl)
                .location(location)
                .mileage(mileage)
                .year(year)
                .build();
    }

    private void getRandomDelay() {
        try {
            long delay = ThreadLocalRandom.current().nextLong(MINIMUM_MS_DELAY, MAX_MS_DELAY);
            log.debug("Random delay set to: {} ms", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

