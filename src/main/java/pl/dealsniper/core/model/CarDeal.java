package pl.dealsniper.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class CarDeal extends BaseDeal {

    private String location;

    private String mileage;

    private Integer year;
}
