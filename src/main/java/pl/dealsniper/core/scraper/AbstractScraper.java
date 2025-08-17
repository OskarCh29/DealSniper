package pl.dealsniper.core.scraper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import pl.dealsniper.core.model.BaseDeal;

@Slf4j
public abstract class AbstractScraper<T extends BaseDeal> implements Scraper<T> {

    private static final int MINIMUM_MS_DELAY = 1000;
    private static final int MAX_MS_DELAY = 60000;

    protected Elements generateUrlRequest(String platformUlr, String offerId) throws IOException {
        Document doc = Jsoup.connect(platformUlr)
                .userAgent(Selector.getRandomUserAgent())
                .header("Accept-Language", Selector.LANGUAGE_HEADER)
                .header("Referer", Selector.getRandomReferer())
                .header("Connection", "keep-alive")
                .timeout(Selector.REQUEST_TIMEOUT)
                .get();
        return doc.select(offerId);
    }

    protected void getRandomDelay() {
        try {
            long delay = ThreadLocalRandom.current().nextLong(MINIMUM_MS_DELAY, MAX_MS_DELAY);
            log.debug("Random delay set to: {} ms", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected String buildPageUrl(String baseUrl, int page) {
        return UriComponentsBuilder.fromUri(URI.create(baseUrl))
                .queryParam("page", page)
                .build()
                .toUriString();
    }

    @Override
    public abstract List<T> getDeals(String platformUrl, Long sourceId);

    @Override
    public abstract T parseElementToDeal(Element element);
}
