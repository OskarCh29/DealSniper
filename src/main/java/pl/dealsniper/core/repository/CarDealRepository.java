/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import java.util.UUID;
import javax.swing.*;
import pl.dealsniper.core.model.BaseDeal;

public interface CarDealRepository<T extends BaseDeal> {
    void save(T deal);

    boolean existsByUserIdAndSourceId(UUID userId, Long sourceId);

    void mergeFromTempTable();

    void deleteInactiveOffers();

    boolean existsByOfferUrlAndSourceId(String offerUrl, Long sourceId);

    List<T> findAllByUserId(UUID userId);

    List<T> findAllByUserIdAndTaskName(UUID userId, String taskName);
}
