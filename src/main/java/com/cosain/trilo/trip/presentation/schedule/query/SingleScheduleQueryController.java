package com.cosain.trilo.trip.presentation.schedule.query;

import com.cosain.trilo.trip.application.schedule.query.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
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
public class SingleScheduleQueryController {

    private final ScheduleDetailSearchUseCase scheduleDetailSearchUseCase;

    @GetMapping("/api/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDetail findSingleSchedule(@PathVariable Long scheduleId) {
        return scheduleDetailSearchUseCase.searchScheduleDetail(scheduleId);
    }
}
