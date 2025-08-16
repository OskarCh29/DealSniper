import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.scraper.otomoto.OtomotoScraper;

import java.util.List;

public class Test {

    public static void main(String[] args) {

        OtomotoScraper scraper = new OtomotoScraper();
        List<CarDeal> list = scraper.getDeals("https://www.otomoto.pl/osobowe/daewoo?search%5Border%5D=relevance_web");

        for (CarDeal d : list) {
            System.out.println(d);
        }
    }
}

