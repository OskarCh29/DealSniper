/* (C) 2025 */
package pl.dealsniper.core.repository;

import pl.dealsniper.core.dto.response.PageResponse;
import pl.dealsniper.core.model.BaseDeal;

import java.util.UUID;

public interface CarDealRepository<T extends BaseDeal> {
    void save(T deal);

    boolean existsByUserIdAndSourceId(UUID userId, Long sourceId);

    void mergeFromTempTable();

    void deleteInactiveOffers();

    boolean existsByOfferUrlAndSourceId(String offerUrl, Long sourceId);

    PageResponse<T> findAllByUserId(UUID userId, int page, int size);

    PageResponse<T> findAllByUserIdAndTaskName(UUID userId, String taskName, int page, int size);
}
