/* (C) 2025 */
package pl.dealsniper.core.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dealsniper.core.exception.RecordNotFoundException;
import pl.dealsniper.core.exception.UserInactiveException;
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
    public List<CarDeal> getUserActiveOffers(UUID userId) {
        List<CarDeal> activeOffers = carDealRepository.findAllByUserId(userId);
        if (activeOffers.isEmpty()) {
            throw new RecordNotFoundException("No offers found for provided user");
        } else {
            return activeOffers;
        }
    }

    private void validateIfUserActiveAndExists(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found"));
        if (!user.getActive()) {
            throw new UserInactiveException("User not active");
        }
    }
}
