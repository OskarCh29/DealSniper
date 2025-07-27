package pl.dealsniper.core.persister;

import pl.dealsniper.core.model.BaseDeal;

public interface DealPersister<T extends BaseDeal> {
    void save(T deal);
}
