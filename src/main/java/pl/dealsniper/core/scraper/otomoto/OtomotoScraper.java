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
import pl.dealsniper.core.util.ScraperUtil;

@Slf4j
@Component
public class OtomotoScraper extends AbstractScraper<CarDeal> {

    private final OtomotoDealParser parser = new OtomotoDealParser();

    private static final int OFFERS_PER_PAGE = 32;
    private static final int MINIMUM_OFFERS_TO_CORRECT_CURRENCY = 2;
    private static final int SECOND_OFFER = 1;

    @Override
    public List<CarDeal> getDeals(String platformUrl, Long sourceId) {
        log.info("Start scraping: {}", platformUrl);
        applyRandomDelay();

        List<CarDeal> carDeals = fetchAllPages(platformUrl, sourceId);

        fixFirstDealCurrencyDueToAdvertisement(carDeals);
        return carDeals;
    }

    @Override
    public CarDeal parseElementToDeal(Element element) {
       return parser.parse(element);
    }

    private void fixFirstDealCurrencyDueToAdvertisement(List<CarDeal> carDeals) {
        if (carDeals.size() >= MINIMUM_OFFERS_TO_CORRECT_CURRENCY) {
            CarDeal first = carDeals.getFirst();
            CarDeal second = carDeals.get(SECOND_OFFER);
            if (first.getCurrency() == null && second.getCurrency() != null) {
                first.setCurrency(second.getCurrency());
            }
        }
    }

    private List<CarDeal> fetchAllPages(String platformUrl, Long sourceId){
        List<CarDeal> carDeals = new ArrayList<>();
        int page = 1;

        while (carDeals.size() < ScraperUtil.MAX_OFFER_RESULT) {
            String pageUrl = buildPageUrl(platformUrl,page);
            Elements listings = loadPageListings(pageUrl);

            if(listings.isEmpty()){
                break;
            }
            addOffers(listings,carDeals,sourceId);

            if(listings.size() < OFFERS_PER_PAGE) {
                break;
            }
            applyRandomDelay();
            page++;
        }
        return carDeals;
    }

    private Elements loadPageListings(String pageUrl) {
        try{
            return generateUrlRequest(pageUrl, OtomotoSelector.OFFER_SELECTOR);
        } catch (IOException e){
            throw new UrlTimeoutException("Request timeout exceeded");
        }
    }

    private void addOffers(Elements elements, List<CarDeal> carDeals, Long sourceId) {
        for (Element element : elements) {
            if (carDeals.size() >= ScraperUtil.MAX_OFFER_RESULT) {
                break;
            }
            CarDeal carDeal = parseElementToDeal(element);
            carDeal.setSourceId(sourceId);
            carDeals.add(carDeal);
        }
    }
}
