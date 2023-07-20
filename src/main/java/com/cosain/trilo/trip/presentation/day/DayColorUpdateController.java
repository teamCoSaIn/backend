package com.cosain.trilo.trip.presentation.day;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.day.service.day_color_update.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.service.day_color_update.DayColorUpdateService;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DayColorUpdateController {

    private final DayColorUpdateService dayColorUpdateService;

    @PutMapping("/api/days/{dayId}/color")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public DayColorUpdateResponse updateDayColor(
            @PathVariable("dayId") Long dayId,
            @LoginUser UserPayload userPayload,
            @RequestBody DayColorUpdateRequest request) {

        long requestTripperId = userPayload.getId();

        var command = DayColorUpdateCommand.of(dayId, requestTripperId, request.getColorName());
        dayColorUpdateService.updateDayColor(command);
        return new DayColorUpdateResponse(dayId);
    }
}
