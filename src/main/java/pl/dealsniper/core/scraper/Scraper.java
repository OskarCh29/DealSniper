package pl.dealsniper.core.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public interface Scraper<T> {

    List<T> getDeals(String platformUrl);

    default Elements generateUrlRequest(String platformUlr, String offerId)
            throws IOException {
        Document doc = Jsoup.connect(platformUlr)
                .userAgent(Selector.getRandomUserAgent())
                .header("Accept-Language", Selector.LANGUAGE_HEADER)
                .header("Referer", Selector.getRandomReferer())
                .header("Connection", "keep-alive")
                .timeout(Selector.REQUEST_TIMEOUT)
                .get();
        return doc.select(offerId);
    }
}
