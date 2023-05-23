package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TemporarySearchUseCase {
    Slice<ScheduleDetail> searchTemporary(Long tripId, Pageable pageable);
}
