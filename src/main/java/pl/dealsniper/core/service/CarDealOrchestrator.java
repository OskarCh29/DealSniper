/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.request.cardeal.CarDealFilterRequest;
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.dto.response.PageResponse;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.model.Source;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.scraper.otomoto.OtomotoScraper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarDealOrchestrator {

    private final UserService userService;
    private final SourceService sourceService;
    private final CarDealService carDealService;
    private final OtomotoScraper otomotoScraper;
    private final EmailService emailService;

    public void processSingleSource(Long sourceId, String taskName) {
        Source source = sourceService.getSourceById(sourceId);
        userService.ensureUserActive(source.getUserId());
        List<CarDeal> scrapedOffers = otomotoScraper.getDeals(source.getFilteredUrl(), source.getId());

        persistScrapedOffers(source.getUserId(), scrapedOffers, taskName);
    }

    public void sendOffersToUserBasedOnFilter(
            UUID userId, CarDealFilterRequest dealFilter, boolean currentActiveRecords, int page, int size) {
        PageResponse<CarDealResponse> deals =
                carDealService.getUserOffersByFilter(userId, dealFilter, currentActiveRecords, page, size);
        User user = userService.getUserById(userId);
        emailService.sendUserFilterOffers(user.getEmail(), deals.content());
    }

    @Transactional
    private void persistScrapedOffers(UUID userId, List<CarDeal> scrappedOffers, String taskName) {
        User user = userService.getUserById(userId);
        List<CarDeal> newOffers = carDealService.loadTempTableAndFindNewDeals(scrappedOffers, userId);
        if (newOffers.isEmpty()) {
            log.info("There are not any new offers...");
        } else {
            log.info("Found {} new offers!", newOffers.size());
            log.info("Preparing new offers to send...");
            emailService.sendOffersToUser(user.getEmail(), newOffers, taskName);
            carDealService.mergeTempTableAndDeleteInactive();
        }
    }
}
