/* (C) 2025 */
package pl.dealsniper.core.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.mapper.CarDealMapper;
import pl.dealsniper.core.service.CarDealOrchestrator;
import pl.dealsniper.core.service.CarDealService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deals")
public class CarDealController {

    private final CarDealOrchestrator orchestrator;
    private final CarDealService carDealService;
    private final CarDealMapper carDealMapper;

    @GetMapping(value = "/{userId}/offers", headers = "Offers=email")
    ResponseEntity<?> sendActiveOffers(@PathVariable UUID userId) {
        orchestrator.sendActiveOffersToUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/offers")
    ResponseEntity<List<CarDealResponse>> getUserActiveOffers(@PathVariable UUID userId) {
        List<CarDealResponse> responses = carDealService.getUserActiveOffers(userId).stream()
                .map(carDealMapper::toCarDealResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
