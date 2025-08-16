package pl.dealsniper.core.mapper;

import org.mapstruct.Mapper;

import com.dealsniper.jooq.tables.records.CarDealsRecord;
import com.dealsniper.jooq.tables.records.CarDealsTmpRecord;

import pl.dealsniper.core.model.CarDeal;

@Mapper(componentModel = "spring")
public interface CarDealMapper {

    CarDeal toDomainCarDeal(CarDealsRecord carDealRecord);

    CarDeal toDomainCarDeal(CarDealsTmpRecord carDealsTmpRecord);

    CarDealsRecord toJooqCarDealRecord(CarDeal carDeal);
}
