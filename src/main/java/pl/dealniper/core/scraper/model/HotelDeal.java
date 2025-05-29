package pl.dealniper.core.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class HotelDeal {

    private String title;

    private String roomType;

    private BigDecimal price;

    private double rate;

    private String offerUrl;

    @Override
    public String toString() {
        return "HotelDeal{" +
                "title='" + title + '\'' +
                ", roomType='" + roomType + '\'' +
                ", price=" + price +
                ", rate=" + rate +
                ", offerUrl='" + offerUrl + '\'' +
                '}';
    }
}
