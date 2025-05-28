package pl.dealniper.core.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CarDeal {

    private String title;

    private BigDecimal price;

    private String location;

    private String mileage;

    private int year;

    private String offerUrl;

    @Override
    public String toString() {
        return "Offer{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", mileage='" + mileage + '\'' +
                ", year='" + year + '\'' +
                ", offerUrl='" + offerUrl + '\'' +
                '}';
    }
}
