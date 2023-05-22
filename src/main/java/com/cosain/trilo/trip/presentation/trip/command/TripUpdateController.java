package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripUpdateRequest;
import com.cosain.trilo.trip.presentation.trip.command.dto.response.TripUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: 여행 수정
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TripUpdateController {

    private final TripUpdateUseCase tripUpdateUseCase;

    @PutMapping("/api/trips/{tripId}")
    @ResponseStatus(HttpStatus.OK)
    public TripUpdateResponse updateTrip(@LoginUser User user, @PathVariable Long tripId, @RequestBody TripUpdateRequest request) {
        Long tripperId = user.getId();
        TripUpdateCommand command = request.toCommand();

        tripUpdateUseCase.updateTrip(tripId, tripperId, command);

        return TripUpdateResponse.from(tripId);
    }
}
