package pl.dealniper.core.scraper.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HotelDeal extends BaseDeal {

    private String roomType;

    private double rate;

    public HotelDeal(String title, BigDecimal price, String offerUrl, String roomType, double rate) {
        super(title, price, offerUrl);
        this.roomType = roomType;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "HotelDeal{" +
                "title='" + this.getTitle() + '\'' +
                ", roomType='" + roomType + '\'' +
                ", price=" + this.getPrice() +
                ", rate=" + rate +
                ", offerUrl='" + this.getOfferUrl() + '\'' +
                '}';
    }
}
