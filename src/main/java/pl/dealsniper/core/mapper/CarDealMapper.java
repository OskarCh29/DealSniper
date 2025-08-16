package pl.dealsniper.core.mapper;

import org.mapstruct.Mapper;
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.model.CarDeal;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarDealMapper {
    CarDealResponse toResponse(CarDeal entity);
    List<CarDealResponse> toResponseList(List<CarDeal> entities);
}
