package pl.dealniper.core.scraper.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OlxDeal extends BaseDeal {

    private String priceWithSymbol;

    private String location;

    public OlxDeal(String title, BigDecimal price, String priceWithSymbol, String offerUrl, String location) {
        super(title, price, offerUrl);
        this.priceWithSymbol = priceWithSymbol;
        this.location = location;
    }

    @Override
    public String toString() {
        return "OlxDeal{" +
                "title= '" + this.getTitle() + '\'' +
                ", price= '" + priceWithSymbol + '\'' +
                ", location= " + location +
                ", offerUrl= '" + this.getOfferUrl() + '\'' +
                '}';
    }
}
