package com.cosain.trilo.trip.infra.repository.trip;

import com.cosain.trilo.trip.domain.dto.TripDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TripQueryRepository {
    Optional<TripDto> findTripDetailByTripId(Long tripId);

    Slice<TripDto> findTripDetailListByTripperId(Long tripperId, Pageable pageable);

    boolean existById(Long tripI);
}
