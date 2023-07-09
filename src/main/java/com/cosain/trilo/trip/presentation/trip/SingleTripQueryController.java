package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.service.TripDetailSearchService;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class SingleTripQueryController {

    private final TripDetailSearchService tripDetailSearchService;

    @GetMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.OK)
    public TripDetail findSingleTrip(@LoginUser User user, @PathVariable Long tripId) {

        Long tripperId = user.getId();

        return tripDetailSearchService.searchTripDetail(tripId, tripperId);
    }
}
