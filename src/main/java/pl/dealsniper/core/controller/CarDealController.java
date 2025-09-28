/* (C) 2025 */
package pl.dealsniper.core.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.dto.response.PageResponse;
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
    ResponseEntity<?> sendActiveOffers(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        orchestrator.sendActiveOffersToUser(userId, page, size);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/offers")
    ResponseEntity<PageResponse<CarDealResponse>> getUserActiveOffers(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<CarDealResponse> response = carDealService.getUserActiveOffers(userId, page, size);

        return ResponseEntity.ok(response);
    }
}
