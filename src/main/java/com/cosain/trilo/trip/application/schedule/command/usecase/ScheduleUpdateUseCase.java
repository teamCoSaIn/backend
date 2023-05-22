package com.cosain.trilo.trip.application.schedule.command.usecase;

import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleUpdateCommand;

public interface ScheduleUpdateUseCase {
    Long updateSchedule( Long scheduleId,Long tripperId,ScheduleUpdateCommand scheduleUpdateCommand);
}
