/* (C) 2025 */
package pl.dealsniper.core.scraper;

import java.util.List;
import org.jsoup.nodes.Element;
import pl.dealsniper.core.model.BaseDeal;

public interface Scraper<T extends BaseDeal> {

    List<T> getDeals(String platformUrl, Long sourceId);

    T parseElementToDeal(Element element);
}
