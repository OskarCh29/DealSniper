package pl.dealsniper.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CarDeal extends BaseDeal {

    private String location;

    private String mileage;

    private Integer year;

    @Override
    public String toString() {
        return "CarDeal{"
                + "title=" + super.getTitle() + '\''
                + "price=" + super.getPrice() + '\''
                + "Currency=" + super.getCurrency() + '\''
                + "URL=" + super.getOfferUrl() + '\''
                + "location='" + location + '\''
                + ", mileage='" + mileage + '\''
                + ", year=" + year
                + '}';
    }
}
