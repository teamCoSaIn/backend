package com.cosain.trilo.unit.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.query.service.TemporarySearchService;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TempSchedulePageCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TemporarySearchServiceTest {

    @InjectMocks
    private TemporarySearchService temporarySearchService;

    @Mock
    private TripQueryRepository tripQueryRepository;

    @Mock
    private ScheduleQueryRepository scheduleQueryRepository;

    @Test
    void tripId가_유효하고_임시보관함이_비어있지_않으면_페이지_요청의_크기만큼의_ScheduleDetail들을_반환한다(){
        // given
        int size = 3;
        Long tripId = 1L;
        Pageable pageable = Pageable.ofSize(size);
        TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(1L);
        ScheduleDetail scheduleDetail1 = new ScheduleDetail(2L, 1L, "제목", "장소", 33.33, 33.33, 1L, "내용", LocalTime.of(12, 10), LocalTime.of(13, 30));
        ScheduleDetail scheduleDetail2 = new ScheduleDetail(3L, 1L, "제목", "장소", 33.33, 33.33, 1L, "내용", LocalTime.of(15, 0 ), LocalTime.of(16, 0));
        ScheduleDetail scheduleDetail3 = new ScheduleDetail(4L, 1L, "제목", "장소", 33.33, 33.33, 1L, "내용", LocalTime.of(16, 0), LocalTime.of(17, 0));
        given(tripQueryRepository.existById(eq(tripId))).willReturn(true);
        given(scheduleQueryRepository.existById(eq(tempSchedulePageCondition.getScheduleId()))).willReturn(true);
        given(scheduleQueryRepository.findTemporaryScheduleListByTripId(eq(tripId), eq(tempSchedulePageCondition) ,eq(pageable))).willReturn(new SliceImpl(List.of(scheduleDetail1, scheduleDetail2, scheduleDetail3)));

        // when
        Slice<ScheduleSummary> scheduleSummaries = temporarySearchService.searchTemporary(tripId,tempSchedulePageCondition,pageable);

        // then
        assertThat(scheduleSummaries.getSize()).isEqualTo(3);
    }
    @Test
    void tripId가_유효하지_않다면_TripNotFoundException을_발생시킨다(){
        // given
        Pageable pageable = PageRequest.of(0, 3);
        TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(1L);
        given(tripQueryRepository.existById(anyLong())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> temporarySearchService.searchTemporary(1L, tempSchedulePageCondition, pageable))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void scheduleId에_해당하는_Schedule이_존재하지_않는다면_ScheduleNotFoundException_에러를_발생시킨다(){

        // given
        Pageable pageable = Pageable.ofSize(3);
        TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(1L);
        given(tripQueryRepository.existById(any())).willReturn(true);
        given(scheduleQueryRepository.existById(any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> temporarySearchService.searchTemporary(1L, tempSchedulePageCondition, pageable))
                .isInstanceOf(ScheduleNotFoundException.class);

    }


}
