package pl.dealsniper.core.persister;

import pl.dealsniper.core.model.BaseDeal;

import java.util.List;

public interface DealPersister<T extends BaseDeal> {
    void save(T deal);

    void saveAll(List<T> deals);

    boolean existsByUrl(String offerUrl);
}
