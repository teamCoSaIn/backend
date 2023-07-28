package com.cosain.trilo.trip.application.dao;

import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;

import java.time.LocalDate;
import java.util.Optional;

public interface TripQueryDAO {

    Optional<TripDetail> findTripDetailById(Long tripId);
    TripListSearchResult findTripSummariesByTripperId(TripListQueryParam queryParam);
    boolean existById(Long tripId);
    TripStatistics findTripStaticsByTripperId(Long tripperId, LocalDate today);
    TripSearchResponse findWithSearchConditions(TripSearchRequest request);
}
