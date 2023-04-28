package com.cosain.trilo.trip.command.presentation.schedule.dto;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    private Long dayId;
    private Long tripId;
    private String title;

    private String placeId;
    private String placeName;
    private double latitude;
    private double longitude;

    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleCreateRequest(Long dayId, Long tripId, String title, String placeId, String placeName, double latitude, double longitude) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.title = title;
        this.placeId = placeId;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ScheduleCreateCommand toCommand() {
        return ScheduleCreateCommand.of(dayId, tripId, title, placeId, placeName, latitude, longitude);
    }

}
