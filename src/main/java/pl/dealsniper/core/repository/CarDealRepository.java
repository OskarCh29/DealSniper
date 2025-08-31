/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import pl.dealsniper.core.model.BaseDeal;

public interface CarDealRepository<T extends BaseDeal> {
    void save(T deal);

    boolean existsByUrl(String offerUrl);

    void mergeFromTempTable();

    void deleteInactiveOffers();

    boolean existsByOfferUrlAndSourceId(String offerUrl, Long sourceId);

    List<T> findAll();
}
