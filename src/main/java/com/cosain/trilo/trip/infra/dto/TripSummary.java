package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripSummary {

    private long tripId;
    private long tripperId;
    private String title;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imagePath;

    @QueryProjection
    public TripSummary(long tripId, long tripperId, String title, Enum status, LocalDate startDate, LocalDate endDate, String imageName) {
        this.tripId = tripId;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status.name();
        this.startDate = startDate;
        this.endDate = endDate;
        this.imagePath = imageName;
    }

    public void updateImagePath(String imagePath){
        this.imagePath = imagePath;
    }
}
