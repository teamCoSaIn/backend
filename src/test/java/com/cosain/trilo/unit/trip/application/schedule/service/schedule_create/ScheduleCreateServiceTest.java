package com.cosain.trilo.unit.trip.application.schedule.service.schedule_create;

import com.cosain.trilo.common.exception.schedule.TooManyDayScheduleException;
import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.application.exception.TooManyTripScheduleException;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateService;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleCreateServiceTest {

    @InjectMocks
    private ScheduleCreateService scheduleCreateService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DayRepository dayRepository;

    @Mock
    private TripRepository tripRepository;

    @Nested
    @DisplayName("Day에 일정을 생성할 때")
    class Case_CreateDaySchedule {

        @Test
        @DisplayName("범위를 넘기지 않을 때, 리포지토리 호출이 한번씩만 이루어지는지 테스트")
        public void when_there_is_no_scheduleIndexRangeException_repository_is_called_only_once() {
            // given
            Long requestTripperId = 1L;
            Long tripId = 2L;
            Long targetDayId = 3L;
            LocalDate startDate = LocalDate.of(2023,3,1);
            LocalDate endDate = LocalDate.of(2023,3,1);
            var command = makeCommand(requestTripperId, tripId, targetDayId);

            // Mock : 리포지토리에서 가져올 Trip, Day
            Trip trip = TripFixture.decided_Id(tripId, requestTripperId, startDate, endDate, targetDayId);
            Day day = trip.getDays().get(0);
            given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

            // Mock : Trip, Day가 가진 Schedule 갯수
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(0);
            given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(0);

            // Mock : 생성될 Schedule
            Schedule createdSchedule = ScheduleFixture.temporaryStorage_Id_NoAdd(1L, trip, 0);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);

            // when : 서비스 측에 일정을 생성하라고 요청할 때
            scheduleCreateService.createSchedule(command);

            // then : 리포지토리 호출 횟수 검증
            verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
            verify(tripRepository, times(1)).findById(eq(tripId));
            verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        }

        @Test
        @DisplayName("범위를 넘길 때, 재배치가 이루어지는 지 테스트")
        public void when_day_scheduleIndex_is_over_limit_then_relocate_called() {
            // given
            Long requestTripperId = 1L;
            Long tripId = 2L;
            Long targetDayId = 3L;
            LocalDate startDate = LocalDate.of(2023,3,1);
            LocalDate endDate = LocalDate.of(2023,3,1);
            var command = makeCommand(requestTripperId, tripId, targetDayId);

            // Mock : 재배치 이전에 가져올 Trip, Day, Day가 가진 Schedule
            Trip beforeTrip = TripFixture.decided_Id(tripId, requestTripperId, startDate, endDate, targetDayId);
            Day beforeDay = beforeTrip.getDays().get(0);
            Schedule beforeSchedule = ScheduleFixture.day_Id(1L, beforeTrip, beforeDay, MAX_INDEX_VALUE);

            // Mock : 재배치 이후 다시 가져올 Trip, Day, Day가 가진 Schedule
            Trip rediscoveredTrip = TripFixture.decided_Id(tripId, requestTripperId, startDate, endDate, targetDayId);
            Day rediscoveredDay = rediscoveredTrip.getDays().get(0);
            Schedule rediscoveredBeforeSchedule = ScheduleFixture.day_Id(1L, rediscoveredTrip, rediscoveredDay, 0L);

            // Mock : 생성된 Schedule
            Schedule createdSchedule = ScheduleFixture.day_Id_notAdd(2L, rediscoveredTrip, rediscoveredDay, DEFAULT_SEQUENCE_GAP);

            when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                    .thenReturn(Optional.of(beforeDay))
                    .thenReturn(Optional.of(rediscoveredDay));

            when(tripRepository.findById(eq(tripId)))
                    .thenReturn(Optional.of(beforeTrip))
                    .thenReturn(Optional.of(rediscoveredTrip));

            given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(1);
            given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(1);

            // when : 서비스 클래스에 일정을 생성해달라고 요청할 때
            scheduleCreateService.createSchedule(command);

            // then : 리포지토리 호출 횟수 검증
            verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
            verify(tripRepository, times(2)).findById(eq(tripId));
            verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        }
    }

    @Nested
    @DisplayName("임시보관함에 일정을 생성할 때")
    class Case_Create_To_TemporaryStorage {

        @Test
        @DisplayName("범위를 넘기지 않을 때, 재배치 없이 리포지토리 호출이 한번씩만 이루어지는지 테스트")
        public void when_there_is_no_scheduleIndexRangeException_repository_is_called_only_once() {
            // given
            long requestTripperId = 1L;
            long tripId = 2L;
            Long targetDayId = null;
            var command = makeCommand(requestTripperId, tripId, targetDayId);

            // Mock : 가져올 Trip
            Trip trip = TripFixture.undecided_Id(tripId, requestTripperId);
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

            // Mock : DB에서 조회하는 값 - Trip이 가지고 있는 Schedule의 갯수
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(0);

            // Mock : 생성될 Schedule
            Schedule createdSchedule = ScheduleFixture.temporaryStorage_Id_NoAdd(1L, trip, 0);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);

            // when : 서비스 클래스에 일정을 생성해달라고 요청할 때
            scheduleCreateService.createSchedule(command);

            // then : 리포지토리 호출 횟수 검증
            verify(dayRepository, times(0)).findByIdWithTrip(isNull());
            verify(tripRepository, times(1)).findById(eq(tripId));
            verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(0)).findDayScheduleCount(isNull());
        }

        @Test
        @DisplayName("임시보관함에서 일정의 순서가 하한선을 벗어날 경우 재배치 기능이 호출되는 지 여부 테스트")
        public void when_temporaryStorage_Schedule_is_under_limit_then_relocate_called() {
            // given
            long requestTripperId = 1L;
            long tripId = 2L;
            Long targetDayId = null;
            var command = makeCommand(requestTripperId, tripId, targetDayId);

            // Mock: 재배치 이전의 Trip과 임시보관함의 Schedule
            Trip trip = TripFixture.undecided_Id(tripId, requestTripperId);
            Schedule beforeSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, MIN_INDEX_VALUE);

            // Mock: 재배치 이후의 Trip과 임시보관함의 Schedule
            Trip rediscoveredTrip = TripFixture.undecided_Id(tripId, requestTripperId);
            Schedule relocatedSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);

            // Mock: 새로 생성될 Schedule
            Schedule newSchedule = ScheduleFixture.temporaryStorage_Id_NoAdd(2L, trip, DEFAULT_SEQUENCE_GAP);

            when(tripRepository.findById(eq(tripId)))
                    .thenReturn(Optional.of(trip))
                    .thenReturn(Optional.of(rediscoveredTrip));
            given(scheduleRepository.relocateDaySchedules(eq(tripId), isNull())).willReturn(1);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(newSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(1);

            // when : 일정을 생성하라고 서비스에 요청할 때
            scheduleCreateService.createSchedule(command);

            // then : 리포지토리 호출 횟수 검증
            verify(dayRepository, times(0)).findByIdWithTrip(isNull());
            verify(tripRepository, times(2)).findById(eq(tripId));
            verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), isNull());
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(0)).findDayScheduleCount(isNull());
        }

    }

    @Test
    @DisplayName("권한 없는 사람이 Schedule을 생성하면, NoScheduleCreateAuthorityException이 발생한다.")
    public void when_no_authority_tripper_create_schedule_it_throws_NoScheduleCreateAuthorityException() {
        // given
        Long tripOwnerId = 1L;
        Long noAuthorityTripperId = 2L;
        Long tripId = 3L;
        Long targetDayId = 4L;
        LocalDate startDate = LocalDate.of(2023,4,1);
        LocalDate endDate = LocalDate.of(2023,4,1);
        var command = makeCommand(noAuthorityTripperId, tripId, targetDayId);

        // mock: 리포지토리에서 가져올 Trip, Day
        Trip trip = TripFixture.decided_Id(tripId, tripOwnerId, startDate, endDate, 1L);
        Day day = trip.getDays().get(0);
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when & then : 발생 예외 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(command))
                .isInstanceOf(NoScheduleCreateAuthorityException.class);
        verify(dayRepository).findByIdWithTrip(eq(targetDayId));
        verify(tripRepository).findById(eq(tripId));
    }

    @Test
    @DisplayName("여행에 일정이 너무 많이 있으면, TooManyTripScheduleException이 발생한다.")
    public void tooManyTripScheduleTest() {
        // given
        long requestTripperId = 1L;
        long tripId = 2L;
        Long targetDayId = null;
        var command = makeCommand(requestTripperId, tripId, targetDayId);

        // mock : 리포지토리에서 가져올 Trip
        Trip trip = TripFixture.undecided_Id(tripId, requestTripperId);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // mock : Trip 아래에 최대 갯수의 Schedule이 있는 상황
        given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(110);

        // when & then : 발생 예외 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(command))
                .isInstanceOf(TooManyTripScheduleException.class);
        verify(dayRepository, times(0)).findByIdWithTrip(isNull());
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(0)).save(any(Schedule.class));
        verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
    }

    @Test
    @DisplayName("Day에 일정이 너무 많이 있으면, TooManyDayScheduleException이 발생한다.")
    public void tooManyDayScheduleTest() {
        // given
        long requestTripperId = 1L;
        long tripId = 2L;
        Long targetDayId = 3L;
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        var command = makeCommand(requestTripperId, tripId, targetDayId);

        // mock : 리포지토리에서 가져올 Trip 및 Day
        Trip trip = TripFixture.decided_Id(tripId, requestTripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // mock : Trip 하위의 일정 갯수
        given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(10);

        // mock : Day 하위의 일정 갯수
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(10);

        // when && then : 발생 예외 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(command))
                .isInstanceOf(TooManyDayScheduleException.class);
        verify(dayRepository, times(0)).findByIdWithTrip(isNull());
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(0)).save(any(Schedule.class));
        verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
    }

    /**
     * 이 테스트 클래스에서만 사용할 ScheduleCreateCommand Fixture입니다.
     * 이 테스트에서 사용할 command에서 유의미한 변경값은 requestTripperId, tripId, targetDayId이므로 해당 값들만 변경하여 사용할 수 있도록 합니다.
     * @param requestTripperId 요청하는 여행자(사용자)의 식별자
     * @param tripId : 일정이 속한 tripId
     * @param targetDayId: 일정이 이동될 대상 dayId
     */
    private ScheduleCreateCommand makeCommand(long requestTripperId, long tripId, Long targetDayId) {
        return ScheduleCreateCommand.of(requestTripperId, tripId, targetDayId, "일정 제목", "place-id", "place-name", 37.123, 123.127);
    }

}
