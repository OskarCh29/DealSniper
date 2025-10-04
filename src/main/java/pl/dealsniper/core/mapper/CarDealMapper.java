/* (C) 2025 */
package pl.dealsniper.core.mapper;

import com.dealsniper.jooq.tables.records.CarDealsRecord;
import com.dealsniper.jooq.tables.records.CarDealsTmpRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.model.CarDeal;

@Mapper(componentModel = "spring")
public interface CarDealMapper {

    CarDeal toDomainCarDeal(CarDealsRecord carDealRecord);

    CarDeal toDomainCarDeal(CarDealsTmpRecord carDealsTmpRecord);

    CarDealsRecord toJooqCarDealRecord(CarDeal carDeal);

    @Mapping(target = "mileage", source = "mileage", qualifiedByName = "mileageWithKilometers")
    CarDealResponse toCarDealResponse(CarDeal carDeal);

    @Named("mileageWithKilometers")
    default String mileageWithKilometers(Integer mileage) {
        return mileage == null ? null : mileage + " km";
    }
}
