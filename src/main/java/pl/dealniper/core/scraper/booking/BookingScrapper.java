package pl.dealniper.core.scraper.booking;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.dealniper.core.exception.UrlTimeoutException;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.model.HotelDeal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingScrapper implements Scraper<HotelDeal> {
    private static final int PRICE_WITHOUT_SYMBOL = 0;

    @Override
    public boolean supports(String platformUrl) {
        return platformUrl.toLowerCase().contains("booking");
    }

    @Override
    public List<HotelDeal> getDeals(String platformUrl) {
        List<HotelDeal> hotelDeals = new ArrayList<>();

        try {
            Elements listings = getUrlElements(platformUrl, BookingSelector.USER_AGENT, BookingSelector.LANGUAGE_HEADER,
                    BookingSelector.REQUEST_TIMEOUT, BookingSelector.OFFER_ID);

            for (Element element : listings) {
                if (hotelDeals.size() >= BookingSelector.MAX_OFFER_RESULT) {
                    break;
                }
                String title = element.select(BookingSelector.OFFER_TITLE).text();

                String roomType = element.select(BookingSelector.OFFER_ROOM_TYPE).text();

                String priceWithCurr = element.select(BookingSelector.OFFER_PRICE).text();
                int convPrice = Integer.parseInt(priceWithCurr.split(" ")[PRICE_WITHOUT_SYMBOL]);
                BigDecimal hotelPrice = BigDecimal.valueOf(convPrice);

                String score = element.select(BookingSelector.OFFER_SCORE).text().replace(",", ".");
                double rate = Double.parseDouble(score);

                String url = element.select(BookingSelector.OFFER_LINK).attr(BookingSelector.OFFER_URL);

                HotelDeal hotelDeal = new HotelDeal(title, hotelPrice, url, roomType, rate);
                hotelDeals.add(hotelDeal);

            }

        } catch (IOException e) {
            throw new UrlTimeoutException("Request timeout exceeded");
        }

        return hotelDeals;
    }
}
