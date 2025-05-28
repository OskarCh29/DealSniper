package pl.dealniper.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.dealniper.core.scraper.otomoto.BodyType;
import pl.dealniper.core.scraper.otomoto.OtomotoScraper;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.model.CarDeal;
import pl.dealniper.core.scraper.otomoto.OtomotoUrl;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        Scraper scraper = new OtomotoScraper();
        OtomotoUrl url = new OtomotoUrl.urlBuilder()
                .bodyType(BodyType.SEDAN)
                .brand("BMW")
                .productionYear(2015)
                .fuelType("petrol")
                .build();
        System.out.println(url.getSearchUrl());
        //List<CarDeal> carDeals = scraper.getDeals(url.getSearchUrl());
        //carDeals.forEach(System.out::println);
    }

}
