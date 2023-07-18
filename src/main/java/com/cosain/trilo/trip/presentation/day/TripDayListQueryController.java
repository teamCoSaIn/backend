package com.cosain.trilo.trip.presentation.day;

import com.cosain.trilo.trip.application.day.service.day_search.DaySearchService;
import com.cosain.trilo.trip.application.day.service.day_search.DayScheduleDetail;
import com.cosain.trilo.trip.presentation.day.dto.DayListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripDayListQueryController {

    private final DaySearchService daySearchService;

    @GetMapping("/api/trips/{tripId}/days")
    @ResponseStatus(HttpStatus.OK)
    public DayListResponse findTripDayList(@PathVariable Long tripId) {
        List<DayScheduleDetail> dayScheduleDetails = daySearchService.searchDaySchedules(tripId);
        return DayListResponse.of(dayScheduleDetails);
    }
}
