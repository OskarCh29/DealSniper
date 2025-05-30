package pl.dealniper.core.scraper.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CarDeal extends BaseDeal {

    private String location;

    private String mileage;

    private int year;

    public CarDeal(String title, BigDecimal price, String offerUrl, String location, String mileage, int year) {
        super(title, price, offerUrl);
        this.location = location;
        this.mileage = mileage;
        this.year = year;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "title='" + this.getTitle() + '\'' +
                ", price=" + this.getPrice() +
                ", location='" + location + '\'' +
                ", mileage='" + mileage + '\'' +
                ", year='" + year + '\'' +
                ", offerUrl='" + this.getOfferUrl() + '\'' +
                '}';
    }
}
