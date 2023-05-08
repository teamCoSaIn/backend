package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.query.application.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;
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
    public TripPageResponse searchTripDetails(Long tripperId, Pageable pageable) {

        verifyTripperExists(tripperId);
        Slice<TripDetail> tripDetailList = findTripDetailList(tripperId, pageable);
        return TripPageResponse.of(mapToTripDetailResponse(tripDetailList), tripDetailList.hasNext());
    }

    private void verifyTripperExists(Long tripperId){
        userRepository.findById(tripperId).orElseThrow(TripperNotFoundException::new);
    }

    private Slice<TripDetail> findTripDetailList(Long tripperId, Pageable pageable){
        return tripQueryRepository.findTripDetailListByTripperId(tripperId, pageable);
    }

    private List<TripDetailResponse> mapToTripDetailResponse(Slice<TripDetail> tripDetailList){
        return tripDetailList.getContent()
                .stream()
                .map(tripDetail -> TripDetailResponse.of(tripDetail.getId(), tripDetail.getTitle(), tripDetail.getStatus(), tripDetail.getStartDate(), tripDetail.getEndDate()))
                .collect(Collectors.toList());
    }
}
