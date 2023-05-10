package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.dto.TripPageResult;
import com.cosain.trilo.trip.query.application.dto.TripResult;
import com.cosain.trilo.trip.query.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.query.application.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.query.domain.dto.TripDto;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import com.cosain.trilo.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripListSearchService implements TripListSearchUseCase {

    private final TripQueryRepository tripQueryRepository;
    private final UserRepository userRepository;

    @Override
    public TripPageResult searchTripDetails(Long tripperId, Pageable pageable) {

        verifyTripperExists(tripperId);
        Slice<TripDto> tripDtos = findTripDtos(tripperId, pageable);
        List<TripResult> tripResults = mapToTripResults(tripDtos);
        return TripPageResult.of(tripResults, tripDtos.hasNext());
    }

    private void verifyTripperExists(Long tripperId){
        userRepository.findById(tripperId).orElseThrow(TripperNotFoundException::new);
    }

    private Slice<TripDto> findTripDtos(Long tripperId, Pageable pageable){
        return tripQueryRepository.findTripDetailListByTripperId(tripperId, pageable);
    }

    private List<TripResult> mapToTripResults(Slice<TripDto> tripDtos){
        return tripDtos.getContent()
                .stream()
                .map(tripDto -> TripResult.from(tripDto))
                .collect(Collectors.toList());
    }
}
