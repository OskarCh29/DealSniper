package pl.dealsniper.core.scraper;

import java.util.List;
import java.util.UUID;

import org.jsoup.nodes.Element;

import pl.dealsniper.core.model.BaseDeal;

public interface Scraper<T extends BaseDeal> {

    List<T> getDeals(String platformUrl, UUID sourceId);

    T parseElementToDeal(Element element);
}
