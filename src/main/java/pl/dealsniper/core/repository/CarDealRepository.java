/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.UUID;
import pl.dealsniper.core.dto.request.cardeal.CarDealFilterRequest;
import pl.dealsniper.core.dto.response.PageResponse;
import pl.dealsniper.core.model.BaseDeal;

public interface CarDealRepository<T extends BaseDeal> {
    void save(T deal);

    boolean existsByUserIdAndSourceId(UUID userId, Long sourceId);

    void mergeFromTempTable();

    void deleteInactiveOffers();

    boolean existsByOfferUrlAndSourceId(String offerUrl, Long sourceId);

    PageResponse<T> findAllByUserId(UUID userId, int page, int size);

    PageResponse<T> findAllByUserIdAndFilter(
            UUID userId, CarDealFilterRequest filter, boolean currentActiveRecords, int page, int size);
}
