package pl.dealsniper.core.scraper.otomoto;

import org.jsoup.nodes.Element;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.util.ScraperUtil;

import java.math.BigDecimal;

public class OtomotoDealParser {

    private static final int LOCATION_START_INDEX = 0;
    private static final int LOCATION_END_INDEX = 1;

    public CarDeal parse(Element element) {
        String title = element.select(OtomotoSelector.OFFER_TITLE).text();

        String textPrice = element.select(OtomotoSelector.OFFER_PRICE).text().replace(" ","");
        BigDecimal price = new BigDecimal(textPrice);

        String offerCurrency = element.select(OtomotoSelector.OFFER_CURRENCY).text();
        if(offerCurrency.isBlank()) {
            offerCurrency = "-";
        }

        String offerUrl = element.select(ScraperUtil.OFFER_URL).attr("href");

        String fullLocation = element.select(OtomotoSelector.OFFER_LOCATION).text();
        int locationEndingIndex = fullLocation.indexOf(")");
        String location = fullLocation.substring(LOCATION_START_INDEX, locationEndingIndex+LOCATION_END_INDEX);

        String rawMileage = element.select(OtomotoSelector.OFFER_MILEAGE).text();
        Integer mileage = Integer.parseInt(rawMileage.replaceAll("\\D+", ""));

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
}
