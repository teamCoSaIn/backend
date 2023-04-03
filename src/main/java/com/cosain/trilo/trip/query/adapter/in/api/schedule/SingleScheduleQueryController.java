package com.cosain.trilo.trip.query.adapter.in.api.schedule;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정 단건 조회
 */
@Slf4j
@RestController
public class SingleScheduleQueryController {

    @GetMapping("/api/schedules/{scheduleId}")
    public String findSingleSchedule(@PathVariable Long scheduleId) {
        throw new NotImplementedException("일정 단건 조회 미구현");
    }
}
