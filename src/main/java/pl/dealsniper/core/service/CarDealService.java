/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.dto.request.cardeal.CarDealFilterRequest;
import pl.dealsniper.core.dto.response.PageResponse;
import pl.dealsniper.core.dto.response.cardeal.CarDealResponse;
import pl.dealsniper.core.exception.db.RecordNotFoundException;
import pl.dealsniper.core.exception.user.UserInactiveException;
import pl.dealsniper.core.mapper.CarDealMapper;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.repository.UserRepository;
import pl.dealsniper.core.repository.impl.CarDealRepositoryImpl;
import pl.dealsniper.core.repository.impl.CarDealTempRepositoryImpl;

@Service
@RequiredArgsConstructor
public class CarDealService {

    private final CarDealRepositoryImpl carDealRepository;
    private final CarDealTempRepositoryImpl tempDealRepository;
    private final UserRepository userRepository;
    private final CarDealMapper carDealMapper;

    @Transactional
    public void mergeTempTableAndDeleteInactive() {
        carDealRepository.mergeFromTempTable();
        carDealRepository.deleteInactiveOffers();
    }

    @Transactional
    public List<CarDeal> loadTempTableAndFindNewDeals(List<CarDeal> offerList, UUID userId) {
        validateIfUserActiveAndExists(userId);
        tempDealRepository.clear();
        tempDealRepository.insertBatch(offerList);
        return tempDealRepository.getNewDeals();
    }

    @Transactional(readOnly = true)
    public PageResponse<CarDealResponse> getUserOffersByFilter(
            UUID userId, CarDealFilterRequest dealFilter, boolean currentActiveRecords, int page, int size) {
        PageResponse<CarDeal> offersByFilter =
                carDealRepository.findAllByUserIdAndFilter(userId, dealFilter, currentActiveRecords, page, size);
        handleEmptyPageResult(offersByFilter.content());
        List<CarDealResponse> responses = offersByFilter.content().stream()
                .map(carDealMapper::toCarDealResponse)
                .toList();
        return PageResponse.<CarDealResponse>builder()
                .content(responses)
                .page(page)
                .size(size)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean checkIfFirstScraping(UUID userId, Long sourceId) {
        return carDealRepository.existsByUserIdAndSourceId(userId, sourceId);
    }

    private void validateIfUserActiveAndExists(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found"));
        if (!user.getActive()) {
            throw new UserInactiveException("User not active");
        }
    }

    private void handleEmptyPageResult(List<CarDeal> responses) {
        if (responses.isEmpty()) {
            throw new RecordNotFoundException("No offers found for provided filters");
        }
    }
}
