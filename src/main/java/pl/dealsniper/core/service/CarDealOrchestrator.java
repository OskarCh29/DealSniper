package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.scraper.otomoto.OtomotoScraper;

@Service
@RequiredArgsConstructor
public class CarDealOrchestrator {

    private final UserService userService;
    private final UserSourceService userSourceService;
    private final CarDealService carDealService;
    private final OtomotoScraper otomotoScraper;

    public void processSingleSource(UUID userId, String scrapedURL) {
        userService.ensureUserActive(userId);
        Source source = userSourceService.getSourceByUserIdAndURL(userId, scrapedURL);

        List<CarDeal> scrapedOffers = otomotoScraper.getDeals(scrapedURL, source.getId());

        persistScrapedOffers(userId, scrapedOffers);
    }

    @Transactional
    public void persistScrapedOffers(UUID userId, List<CarDeal> scrappedOffers) {
        List<CarDeal> newOffers = carDealService.loadTempTableAndFindNewDeals(scrappedOffers, userId);
        carDealService.mergeTempTableAndDeleteInactive();
    }
}
