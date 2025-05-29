package pl.dealniper.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.booking.BookingScrapper;
import pl.dealniper.core.scraper.model.HotelDeal;
import pl.dealniper.core.scraper.otomoto.BodyType;
import pl.dealniper.core.scraper.otomoto.OtomotoScraper;
import pl.dealniper.core.scraper.otomoto.OtomotoUrl;

import java.util.List;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        Scraper<HotelDeal> scraper = new BookingScrapper();
        String url = "https://www.booking.com/searchresults.pl.html?label=gen173nr-1BCAEoggI46AdIM1gEaLYBiAEBmAEeuAEXyAEM2AEB6AEBiAIBqAIDuAKU2eDBBsACAdICJDA5MWM5MjRkLTFkM2QtNDdlZi04ZWMzLTQwOWI2NDRhMjM4YtgCBeACAQ&sid=64895904a76c0db9e05ffc1094b51849&aid=304142&ss=Wroc%C5%82aw&ssne=Wroc%C5%82aw&ssne_untouched=Wroc%C5%82aw&lang=pl&src=index&dest_id=-537080&dest_type=city&checkin=2025-05-30&checkout=2025-05-31&group_adults=2&no_rooms=1&group_children=0&nflt=price%3DPLN-min-190-1%3Btdb%3D3";
        List<HotelDeal> hotelDeals = scraper.getDeals(url);
        hotelDeals.forEach(System.out::println);
    }

}
