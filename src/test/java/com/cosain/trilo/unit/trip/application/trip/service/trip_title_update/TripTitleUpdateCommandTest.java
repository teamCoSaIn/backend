package com.cosain.trilo.unit.trip.application.trip.service.trip_title_update;


import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.service.trip_title_update.TripTitleUpdateCommand;
import com.cosain.trilo.common.exception.trip.InvalidTripTitleException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@Slf4j
@DisplayName("TripTitleUpdateCommandFactory 테스트")
public class TripTitleUpdateCommandTest {

    @DisplayName("올바른 길이의 제목 -> 정상 생성")
    @Test
    public void createSuccessTest() {
        // given
        String normalTitle = "제목";

        // when
        var command = makeCommand(normalTitle);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getTripTitle()).isEqualTo(TripTitle.of(normalTitle));
    }

    @Test
    @DisplayName("제목이 Null -> 검증에러 발생")
    public void testNullTitle() {
        // given
        String nullTitle = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> makeCommand(nullTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제목이 Null -> 검증에러 발생")
    @Test
    public void emptyTitle() {
        // given
        String emptyTitle = "";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> makeCommand(emptyTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제목 공백으로만 구성 -> 검증예외 발생")
    @Test
    public void whiteSpaceTitle() {
        // given
        String whiteSpaceTitle = "    ";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> makeCommand(whiteSpaceTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("20자보다 긴 제목 -> 검증예외 발생")
    @Test
    public void tooLongTitle() {
        // given
        String tooLongTitle = "가".repeat(21);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> makeCommand(tooLongTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    private TripTitleUpdateCommand makeCommand(String rawTitle) {
        long tripId = 1L;
        long tripperId = 2L;
        return TripTitleUpdateCommand.of(tripId, tripperId, rawTitle);
    }

}
