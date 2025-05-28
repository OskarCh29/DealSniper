package pl.dealniper.core.scraper;

import java.util.List;

public interface Scraper<T> {

    boolean supports(String platformUrl);

    List<T> getDeals(String platformUrl);
}
