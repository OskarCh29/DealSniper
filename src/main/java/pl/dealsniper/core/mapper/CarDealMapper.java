/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.CarDealsRecord;
import com.dealsniper.jooq.tables.records.CarDealsTmpRecord;
import org.mapstruct.Mapper;
import pl.dealsniper.core.model.CarDeal;

@Mapper(componentModel = "spring")
public interface CarDealMapper {

    CarDeal toDomainCarDeal(CarDealsRecord carDealRecord);

    CarDeal toDomainCarDeal(CarDealsTmpRecord carDealsTmpRecord);

    CarDealsRecord toJooqCarDealRecord(CarDeal carDeal);
}
