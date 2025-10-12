/* (C) 2025 */
package pl.dealsniper.core.scraper.otomoto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.dealsniper.core.exception.UrlTimeoutException;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.scraper.AbstractScraper;

@Slf4j
@Component
public class OtomotoScraper extends AbstractScraper<CarDeal> {

    private static final int OFFERS_PER_PAGE = 32;
    private static final int MINIMUM_OFFERS_TO_CORRECT_CURRENCY = 2;

    @Override
    public List<CarDeal> getDeals(String platformUrl, Long sourceId) {
        getRandomDelay();
        List<CarDeal> carDeals = new ArrayList<>();

        int page = 1;
        boolean morePages = true;

        log.info("Start scraping: {}", platformUrl);

        try {
            while (morePages && carDeals.size() < OtomotoSelector.MAX_OFFER_RESULT) {
                String pageUrl = buildPageUrl(platformUrl, page);

                Elements listings = generateUrlRequest(pageUrl, OtomotoSelector.OFFER_SELECTOR);

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
                    CarDeal deal = parseElementToDeal(element);
                    deal.setSourceId(sourceId);

                    carDeals.add(deal);
                }
                if (listings.size() == OFFERS_PER_PAGE) {
                    getRandomDelay();
                    page++;
                } else {
                    morePages = false;
                }
            }
        } catch (IOException e) {
            log.error("Request time exceeded: {}", e.getMessage());
            throw new UrlTimeoutException("Request timeout exceeded");
        }
        fixFirstDealCurrencyDueToAdvertisement(carDeals);
        return carDeals;
    }

    @Override
    public CarDeal parseElementToDeal(Element element) {
        String title = element.select(OtomotoSelector.OFFER_TITLE).text();

        String textPrice = element.select(OtomotoSelector.OFFER_PRICE).text().replace(" ", "");
        BigDecimal price = new BigDecimal(textPrice);

        String offerCurrency = element.select(OtomotoSelector.OFFER_CURRENCY).text();
        if (offerCurrency.isBlank()) {
            offerCurrency = null;
        }

        String offerUrl = element.select(OtomotoSelector.OFFER_URL).attr("href");

        String fullLocation = element.select(OtomotoSelector.OFFER_LOCATION).text();
        int locationEndingIndex = fullLocation.indexOf(")");
        String location = fullLocation.substring(0, locationEndingIndex + 1);

        String rawMileage = element.select(OtomotoSelector.OFFER_MILEAGE).text();
        Integer mileage = Integer.parseInt(rawMileage.replaceAll("\\D+", ""));

        Integer year = Integer.parseInt(
                element.select(OtomotoSelector.OFFER_PRODUCTION_YEAR).text());

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

    private void fixFirstDealCurrencyDueToAdvertisement(List<CarDeal> carDeals) {
        if (carDeals.size() >= MINIMUM_OFFERS_TO_CORRECT_CURRENCY) {
            CarDeal first = carDeals.getFirst();
            CarDeal second = carDeals.get(1);
            if (first.getCurrency() == null && second.getCurrency() != null) {
                first.setCurrency(second.getCurrency());
            }
        }
    }
}
