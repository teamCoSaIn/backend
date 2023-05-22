package com.cosain.trilo.trip.presentation.schedule.command.dto.request;

import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleMoveRequest {

    private Long targetDayId;
    private int targetOrder;

    public ScheduleMoveRequest(Long targetDayId, int targetOrder) {
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }

    public ScheduleMoveCommand toCommand() {
        return ScheduleMoveCommand.of(targetDayId, targetOrder);
    }
}
