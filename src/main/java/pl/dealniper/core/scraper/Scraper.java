package pl.dealniper.core.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public interface Scraper<T> {

    boolean supports(String platformUrl);

    List<T> getDeals(String platformUrl);

    default Elements getUrlElements(String platformUlr, String userAgent, String headerLanguage, int timeout,
                                    String offerId)
            throws IOException {
        Document doc = Jsoup.connect(platformUlr)
                .userAgent(userAgent)
                .header("Accept-Language", headerLanguage)
                .header("Connection", "keep-alive")
                .timeout(timeout)
                .get();
        return doc.select(offerId);
    }
}
