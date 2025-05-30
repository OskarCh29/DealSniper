package pl.dealniper.core.scraper.olx;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.dealniper.core.exception.UrlTimeoutException;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.model.OlxDeal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OlxScrapper implements Scraper<OlxDeal> {

    private static final int LOCATION_WITHOUT_DATE = 0;

    @Override
    public boolean supports(String platformUrl) {
        return platformUrl.toLowerCase().contains("olx");
    }

    @Override
    public List<OlxDeal> getDeals(String platformUrl) {
        List<OlxDeal> olxDeals = new ArrayList<>();

        try {
            Elements elements = getUrlElements(platformUrl, OlxSelector.USER_AGENT, OlxSelector.LANGUAGE_HEADER,
                    OlxSelector.REQUEST_TIMEOUT, OlxSelector.OFFER_ID);

            for (Element element : elements) {
                if (olxDeals.size() >= OlxSelector.MAX_OFFER_RESULT) {
                    break;
                }
                if(OlxSelector.isOfferPaid(element)){
                    continue;
                }
                String title = element.select(OlxSelector.OFFER_TITLE).text();

                String priceWithSymbol = element.select(OlxSelector.OFFER_PRICE).text();
                BigDecimal price = parsePriceWithSymbol(priceWithSymbol);

                String offerUrl = OlxSelector.BASE_URL + element.select(OlxSelector.OFFER_LINK)
                        .attr(OlxSelector.OFFER_URL);

                String location = element.select(OlxSelector.OFFER_LOCATION)
                        .text().split("-")[LOCATION_WITHOUT_DATE];


                OlxDeal deal = new OlxDeal(title, price, priceWithSymbol, offerUrl, location);
                olxDeals.add(deal);

            }
        } catch (IOException e) {
            throw new UrlTimeoutException("Request timeout exceeded");
        }
        return olxDeals;
    }

    ///  Adjust specific exception later
    private BigDecimal parsePriceWithSymbol(String priceWithSymbol) {
        if (priceWithSymbol.equals("Za darmo") || priceWithSymbol.isBlank()) {
            return BigDecimal.ZERO;
        }
        String withoutSymbol =  priceWithSymbol
                .replaceAll("[^\\d,\\.]", "")
                .replace(",", ".");

        try {
            return new BigDecimal(withoutSymbol);
        } catch (NumberFormatException e){
            throw new RuntimeException("Invalid price");
        }
    }
}
