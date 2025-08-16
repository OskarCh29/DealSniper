package pl.dealsniper.core.repository;

import java.util.List;
import java.util.UUID;

import pl.dealsniper.core.model.BaseDeal;

public interface CarDealRepository<T extends BaseDeal> {
    void save(T deal);

    boolean existsByUrl(String offerUrl);

    void mergeFromTempTable();

    void deleteInactiveOffers();

    boolean existsByOfferUrlAndSourceId(String offerUrl, UUID sourceId);

    List<T> findAll();
}
