package pl.dealsniper.core.persister;

import java.util.List;

import pl.dealsniper.core.model.BaseDeal;

public interface DealPersister<T extends BaseDeal> {
    void save(T deal);

    void saveAll(List<T> deals);

    boolean existsByUrl(String offerUrl);
}
