package pl.dealniper.core.scraper.otomoto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.dealniper.core.scraper.Scraper;
import pl.dealniper.core.scraper.model.CarDeal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OtomotoScraper implements Scraper {

    @Override
    public boolean supports(String platformUrl) {
        return "otomoto".equalsIgnoreCase(platformUrl);
    }

    @Override
    public List<CarDeal> getDeals(String platformUrl) {
        List<CarDeal> carDeals = new ArrayList<>();
        int servicePage = 1;

        while (servicePage <=3) {
            try {
                String paginatedUrl = addPageParam(platformUrl, servicePage);
                Document doc = Jsoup.connect(paginatedUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();
                Elements listings = doc.select("article[data-id]");

                if (listings.isEmpty()) {
                    break;
                }

                for (Element element : listings) {
                    String title = element.select("h2").text();

                    String url = element.select("a").attr("href");

                    String locationTag = element.select("p[class*=ooa-oj1jk2]").text();
                    String[] words = locationTag.split(" ");
                    String location = locationTag.length() >= 2 ? words[0] + " " + words[1] : locationTag;

                    String priceStr = element.select("h3[class*=ooa-1n2paoq]").text().replace(" ", "");
                    BigDecimal price = new BigDecimal(priceStr.isEmpty() ? "0" : priceStr);

                    String mileage = element.select("dd[data-parameter=mileage]").text();

                    String productionYear = element.select("dd[data-parameter=year]").text();
                    int year = Integer.parseInt(productionYear);

                    carDeals.add(new CarDeal(title, price, location, mileage, year, url));
                }
                servicePage++;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return carDeals;
    }

    private String addPageParam(String url, int page) {
        if (url.contains("?")) {
            return url + "&page=" + page;
        } else {
            return url + "?page=" + page;
        }
    }
}
