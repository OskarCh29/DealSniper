/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.scraper.otomoto.OtomotoScraper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarDealOrchestrator {

    private final UserService userService;
    private final UserSourceService userSourceService;
    private final CarDealService carDealService;
    private final OtomotoScraper otomotoScraper;
    private final EmailService emailService;

    public void processSingleSource(UUID userId, String scrapedURL) {
        userService.ensureUserActive(userId);
        Source source = userSourceService.getSourceByUserIdAndURL(userId, scrapedURL);

        List<CarDeal> scrapedOffers = otomotoScraper.getDeals(scrapedURL, source.getId());

        persistScrapedOffers(userId, scrapedOffers);
    }

    @Transactional
    private void persistScrapedOffers(UUID userId, List<CarDeal> scrappedOffers) {
        User user = userService.getUserById(userId);
        List<CarDeal> newOffers = carDealService.loadTempTableAndFindNewDeals(scrappedOffers, userId);
        log.info("Preparing new offers to send...");
        emailService.sendOffersToUser(user.getEmail(), newOffers);
        carDealService.mergeTempTableAndDeleteInactive();
    }
}
