package com.cosain.trilo.unit.trip.command.application.service.schedule;

import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;
import com.cosain.trilo.trip.command.application.exception.NoScheduleUpdateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.command.application.service.ScheduleUpdateService;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduleUpdateServiceTest {

    @InjectMocks
    private ScheduleUpdateService scheduleUpdateService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("호출이 제대로 이루어 지는지 테스트")
    public void update_schedule_test(){
        // given
        ScheduleUpdateCommand command = ScheduleUpdateCommand.of("변경할 제목", "변경할 내용");

        Schedule schedule = Schedule.builder()
                .trip(Trip.create("여행 제목", 1L))
                .title("원래 제목")
                .content("원래 내용")
                .build();

        given(scheduleRepository.findByIdWithTrip(anyLong())).willReturn(Optional.of(schedule));
        // when
        scheduleUpdateService.updateSchedule(1L, 1L, command);

        // then
        verify(scheduleRepository).findByIdWithTrip(anyLong());
    }

    @Test
    @DisplayName("권한이 없는 사람이 Schedule 을 변경하려고 하면, NoScheduleUpdateAuthorityException 이 발생한다")
    public void when_no_authority_user_try_update_test(){
        // given
        ScheduleUpdateCommand command = ScheduleUpdateCommand.of("변경할 제목", "변경할 내용");

        Schedule schedule = Schedule.builder()
                .trip(Trip.create("여행 제목", 2L))
                .title("원래 제목")
                .content("원래 내용")
                .build();

        given(scheduleRepository.findByIdWithTrip(anyLong())).willReturn(Optional.of(schedule));
        // when & then
        Assertions.assertThatThrownBy(() -> scheduleUpdateService.updateSchedule(1L, 1L, command))
                .isInstanceOf(NoScheduleUpdateAuthorityException.class);
    }

    @Test
    @DisplayName("일정이 존재하지 않는다면, ScheduleNotFoundException 이 발생한다")
    public void when_no_schedule_test(){
        // given
        ScheduleUpdateCommand command = ScheduleUpdateCommand.of("변경할 제목", "변경할 내용");
        given(scheduleRepository.findByIdWithTrip(anyLong())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> scheduleUpdateService.updateSchedule(1L, 1L, command))
                .isInstanceOf(ScheduleNotFoundException.class);
    }
}
