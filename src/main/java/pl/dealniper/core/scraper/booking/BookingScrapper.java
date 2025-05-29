package pl.dealniper.core.scraper.booking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
        return "booking".equalsIgnoreCase(platformUrl);
    }

    @Override
    public List<HotelDeal> getDeals(String platformUrl) {
        List<HotelDeal> hotelDeals = new ArrayList<>();
        int servicePage = 1;

        while (servicePage <= BookingSelector.MAX_OFFER_PAGE) {
            try {
                String paginatedUrl = addPageParam(platformUrl, servicePage);
                Document doc = Jsoup.connect(paginatedUrl)
                        .userAgent(BookingSelector.USER_AGENT)
                        .header("Accept-Language", BookingSelector.LANGUAGE_HEADER)
                        .header("Connection", "keep-alive")
                        .timeout(BookingSelector.REQUEST_TIMEOUT)
                        .get();
                Elements listings = doc.select(BookingSelector.OFFER_ID);

                if (listings.isEmpty()) {
                    break;
                }

                for (Element element : listings) {
                    Element titleElement = element.selectFirst(BookingSelector.OFFER_TITLE);
                    String title = titleElement != null ? titleElement.text() : "Property name missing";

                    Element roomElement = element.selectFirst("");
                    String roomType = roomElement != null ? roomElement.text() : "Room type missing";

                    Element priceElement = element.selectFirst(BookingSelector.OFFER_PRICE);
                    String priceWithCurr = priceElement != null ? priceElement.text() : "Price missing";
                    int convPrice = Integer.parseInt(priceWithCurr.split(" ")[PRICE_WITHOUT_SYMBOL]);
                    BigDecimal hotelPrice = BigDecimal.valueOf(convPrice);


                    Element rateElement = element.selectFirst(BookingSelector.OFFER_SCORE);
                    String score = rateElement != null ?
                            rateElement.text().replace(",", ".") : "Rate missing";
                    double rate = Double.parseDouble(score);

                    String url = element.select(BookingSelector.OFFER_LINK).attr(BookingSelector.OFFER_URL);

                    HotelDeal hotelDeal = new HotelDeal(title, roomType, hotelPrice, rate, url);
                    hotelDeals.add(hotelDeal);
                }
                servicePage++;
            } catch (IOException e) {
                throw new UrlTimeoutException("Request timeout exceeded");
            }
        }
        return hotelDeals;
    }

    private String addPageParam(String url, int page) {
        if (url.contains("?")) {
            return url + "&page=" + page;
        } else {
            return url + "?page=" + page;
        }
    }
}
