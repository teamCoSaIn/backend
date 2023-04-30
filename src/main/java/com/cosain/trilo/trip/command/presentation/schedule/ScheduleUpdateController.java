package com.cosain.trilo.trip.command.presentation.schedule;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;
import com.cosain.trilo.trip.command.application.usecase.ScheduleUpdateUseCase;
import com.cosain.trilo.trip.command.presentation.schedule.dto.ScheduleUpdateRequest;
import com.cosain.trilo.trip.command.presentation.schedule.dto.ScheduleUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScheduleUpdateController {

    private final ScheduleUpdateUseCase scheduleUpdateUseCase;

    @PutMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleUpdateResponse updateSchedule(@LoginUser User user, @PathVariable Long scheduleId, @RequestBody ScheduleUpdateRequest scheduleUpdateRequest) {
        Long tripperId = user.getId();

        ScheduleUpdateCommand scheduleUpdateCommand = scheduleUpdateRequest.toCommand();
        Long updatedScheduleId = scheduleUpdateUseCase.updateSchedule(scheduleId,tripperId, scheduleUpdateCommand);

        return ScheduleUpdateResponse.from(updatedScheduleId);
    }
}
