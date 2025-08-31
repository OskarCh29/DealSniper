/* (C) 2025 */
package pl.dealsniper.core.repository;

import java.util.List;
import pl.dealsniper.core.model.BaseDeal;
import pl.dealsniper.core.model.CarDeal;

public interface CarDealTempRepository<T extends BaseDeal> {

    void clear();

    void insertBatch(List<T> deals);

    List<CarDeal> getNewDeals();
}
