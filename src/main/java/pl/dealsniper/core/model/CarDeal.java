package pl.dealsniper.core.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarDeal extends BaseDeal {

    private String location;

    private String mileage;

    private int year;

    public CarDeal(
            String dealTitle, BigDecimal dealPrice, String dealOfferUrl,
            String dealLocation, String dealMileage, int dealYear) {
        super(null, dealTitle, dealPrice, dealOfferUrl);
        this.location = dealLocation;
        this.mileage = dealMileage;
        this.year = dealYear;
    }

}
