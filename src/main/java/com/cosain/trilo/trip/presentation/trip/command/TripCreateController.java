package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.TripCreateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripCreateCommandFactory;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripCreateRequest;
import com.cosain.trilo.trip.presentation.trip.command.dto.response.TripCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripCreateController {

    private final TripCreateUseCase tripCreateUseCase;
    private final TripCreateCommandFactory tripCreateCommandFactory;

    @PostMapping("/api/trips")
    public ResponseEntity<TripCreateResponse> createTrip(@LoginUser User user, @RequestBody TripCreateRequest request) {
        Long tripperId = user.getId();
        TripCreateCommand createCommand = tripCreateCommandFactory.createCommand(request.getTitle());

        Long tripId = tripCreateUseCase.createTrip(tripperId, createCommand);

        var response = TripCreateResponse.from(tripId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
