package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleMoveRequest;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.DEFAULT_SEQUENCE_GAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 일정 이동 기능에 대한 통합 테스트 클래스입니다.
 */
@DisplayName("[통합] 일정 이동 API 테스트")
public class ScheduleMoveIntegrationTest extends IntegrationTest {

    /**
     * 임시보관함에서 Day로 이동하는 기능이 잘 동작하는지 테스트합니다.
     */
    @Test
    @DisplayName("임시보관함에서 Day로 이동 -> 일정 이동됨")
    public void testTemporaryToDay() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupDecidedTrip(user.getId(), LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1));
        Day targetDay = trip.getDays().get(0);
        Schedule targetDaySchedule0 = setupDaySchedule(trip, targetDay, 0);
        Schedule targetDaySchedule1 = setupDaySchedule(trip, targetDay, 1);
        Schedule temporarySchedule = setupTemporarySchedule(trip, 0);
        flushAndClear();

        var request = new ScheduleMoveRequest(targetDay.getId(), 1);

        // when
        // targetDay의 1번 일정 앞으로 임시보관함 일정을 이동(중간삽입)
        ResultActions resultActions = runTest(temporarySchedule.getId(), createRequestJson(request), user);
        flushAndClear();

        // then
        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(temporarySchedule.getId()))
                .andExpect(jsonPath("$.beforeDayId").doesNotExist())
                .andExpect(jsonPath("$.afterDayId").value(targetDay.getId()))
                .andExpect(jsonPath("$.positionChanged").value(true));

        // targetDay의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveDaySchedules(targetDay.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(targetDaySchedule0.getId(), temporarySchedule.getId(), targetDaySchedule1.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, DEFAULT_SEQUENCE_GAP/2, DEFAULT_SEQUENCE_GAP);
    }

    /**
     * Day에서 다른 Day로 이동하는 기능이 잘 동작하는지 테스트합니다.
     */
    @Test
    @DisplayName("Day에서 다른 Day로 이동 -> 일정 이동됨")
    public void testDayToOtherDay() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupDecidedTrip(user.getId(), LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2));
        Day fromDay = trip.getDays().get(0);
        Day targetDay = trip.getDays().get(1);
        Schedule fromDaySchedule = setupDaySchedule(trip, fromDay, 0L);
        Schedule targetDaySchedule0 = setupDaySchedule(trip, targetDay, 10000);
        Schedule targetDaySchedule1 = setupDaySchedule(trip, targetDay, 20000);
        flushAndClear();

        var request = new ScheduleMoveRequest(targetDay.getId(), 2);

        // when : targetDay의 맨 뒤로 일정 이동
        ResultActions resultActions = runTest(fromDaySchedule.getId(), createRequestJson(request), user);
        flushAndClear();

        // then

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(fromDaySchedule.getId()))
                .andExpect(jsonPath("$.beforeDayId").value(fromDay.getId()))
                .andExpect(jsonPath("$.afterDayId").value(targetDay.getId()))
                .andExpect(jsonPath("$.positionChanged").value(true));

        // targetDay의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveDaySchedules(targetDay.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(targetDaySchedule0.getId(), targetDaySchedule1.getId(), fromDaySchedule.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(10000L, 20000L, 20000L + DEFAULT_SEQUENCE_GAP);
    }

    /**
     * Day에서 같은 Day의 다음 순서로 이동 요청시, 제자리 이동됨을 검증합니다.
     */
    @Test
    @DisplayName("Day에서 같은 Day의 같은 순서로 이동 -> 제자리 이동")
    public void testDayToSameDay_and_SameOrder() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupDecidedTrip(user.getId(), LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1));
        Day day = trip.getDays().get(0);
        Schedule schedule0 = setupDaySchedule(trip, day, 0L);
        Schedule schedule1 = setupDaySchedule(trip, day, 10000L);
        Schedule schedule2 = setupDaySchedule(trip, day, 20000L);
        flushAndClear();

        var request = new ScheduleMoveRequest(day.getId(), 1);

        // when : targetDay의 맨 뒤로 일정 이동
        ResultActions resultActions = runTest(schedule1.getId(), createRequestJson(request), user);
        flushAndClear();

        // then
        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(schedule1.getId()))
                .andExpect(jsonPath("$.beforeDayId").value(day.getId()))
                .andExpect(jsonPath("$.afterDayId").value(day.getId()))
                .andExpect(jsonPath("$.positionChanged").value(false));

        // targetDay의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveDaySchedules(day.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(schedule0.getId(), schedule1.getId(), schedule2.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, 10000L, 20000L);
    }

    /**
     * Day에서 같은 Day의 바로 다음 순서로 이동하면 제자리 이동됨을 검증합니다.
     */
    @Test
    @DisplayName("Day에서 같은 Day의 다음 순서로 이동 -> 제자리 이동")
    public void testDayToSameDay_and_NextOrder() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupDecidedTrip(user.getId(), LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1));
        Day day = trip.getDays().get(0);
        Schedule schedule0 = setupDaySchedule(trip, day, 0L);
        Schedule schedule1 = setupDaySchedule(trip, day, 10000L);
        Schedule schedule2 = setupDaySchedule(trip, day, 20000L);
        flushAndClear();

        var request = new ScheduleMoveRequest(day.getId(), 2);

        // when
        // 제자리 이동
        ResultActions resultActions = runTest(schedule1.getId(), createRequestJson(request), user);
        flushAndClear();

        // then
        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(schedule1.getId()))
                .andExpect(jsonPath("$.beforeDayId").value(day.getId()))
                .andExpect(jsonPath("$.afterDayId").value(day.getId()))
                .andExpect(jsonPath("$.positionChanged").value(false));

        // targetDay의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveDaySchedules(day.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(schedule0.getId(), schedule1.getId(), schedule2.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, 10000L, 20000L);
    }

    /**
     * 임시보관함에서 같은 순서로 이동하면, 제자리 이동됨을 검증합니다.
     */
    @Test
    @DisplayName("임시보관함에서 동일한 순서로 이동 -> 제자리 이동")
    public void testTemporaryToTemporary_and_SameOrder() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Schedule schedule0 = setupTemporarySchedule(trip, 0L);
        Schedule schedule1 = setupTemporarySchedule(trip,10000L);
        Schedule schedule2 = setupTemporarySchedule(trip, 20000L);
        flushAndClear();

        var request = new ScheduleMoveRequest(null, 1);

        // when
        // 임시보관함 동일한 순서로 이동
        ResultActions resultActions = runTest(schedule1.getId(), createRequestJson(request), user);
        flushAndClear();

        // then
        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(schedule1.getId()))
                .andExpect(jsonPath("$.beforeDayId").doesNotExist())
                .andExpect(jsonPath("$.afterDayId").doesNotExist())
                .andExpect(jsonPath("$.positionChanged").value(false));

        // 임시보관함의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveTemporarySchedules(trip.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(schedule0.getId(), schedule1.getId(), schedule2.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, 10000L, 20000L);
    }

    /**
     * 임시보관함에서 다음 순서로 이동할 때, 제자리 이동됨을 검증합니다.
     */
    @Test
    @DisplayName("임시보관함에서 다음 순서로 이동 -> 제자리 이동")
    public void testTemporaryToTemporary_and_NextOrder() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Schedule schedule0 = setupTemporarySchedule(trip, 0L);
        Schedule schedule1 = setupTemporarySchedule(trip,10000L);
        Schedule schedule2 = setupTemporarySchedule(trip, 20000L);
        flushAndClear();

        var request = new ScheduleMoveRequest(null, 2);

        // when : targetDay의 맨 뒤로 일정 이동
        ResultActions resultActions = runTest(schedule1.getId(), createRequestJson(request), user);
        flushAndClear();

        // then ===================================================================================
        // then1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(schedule1.getId()))
                .andExpect(jsonPath("$.beforeDayId").doesNotExist())
                .andExpect(jsonPath("$.afterDayId").doesNotExist())
                .andExpect(jsonPath("$.positionChanged").value(false));

        // then2: 임시보관함의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveTemporarySchedules(trip.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(3);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(schedule0.getId(), schedule1.getId(), schedule2.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, 10000L, 20000L);
    }

    /**
     * 미인증 사용차 요청이 왔을 때 토큰이 없다는 응답이 전달됨을 검증합니다.
     */
    @Test
    @DisplayName("토큰 없는 미인증 사용자 요청 -> 인증 실패 401")
    public void updateSchedulePlace_with_unauthorizedUser() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupUndecidedTrip(user.getId());
        Schedule schedule0 = setupTemporarySchedule(trip, 0L);
        Schedule schedule1 = setupTemporarySchedule(trip,10000L);
        flushAndClear();

        var request = new ScheduleMoveRequest(null, 2);

        // when
        // 미인증 사용자의 요청
        ResultActions resultActions = runTestWithUnAuthorization(schedule0.getId(), createRequestJson(request));
        flushAndClear();

        // then ====================================================================================

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 임시보관함의 일정들 순서 검증
        List<Schedule> retrievedSchedules = retrieveTemporarySchedules(trip.getId());
        assertThat(retrievedSchedules.size()).isEqualTo(2);
        assertThat(retrievedSchedules).map(Schedule::getId)
                .containsExactly(schedule0.getId(), schedule1.getId());
        assertThat(retrievedSchedules).map(sch -> sch.getScheduleIndex().getValue())
                .containsExactly(0L, 10000L);
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param scheduleId 일정의 식별자
     * @param content : 요청 본문(body)
     * @param requestUser : 요청 사용자
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object scheduleId, String content, User requestUser) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 토큰이 없는(미인증) 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param scheduleId 일정의 식별자
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithUnAuthorization(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 전달받은 Day 식별자(dayId)에 대응하는 Day의 Schedule들을 모두 얻어옵니다.
     * @param dayId : Day의 식별자
     * @return Day의 일정들
     */
    private List<Schedule> retrieveDaySchedules(Long dayId) {
        return em.createQuery("""
                        SELECT s
                        FROM Schedule  s
                        WHERE s.day.id = :dayId
                        ORDER BY s.scheduleIndex.value asc
                        """, Schedule.class)
                .setParameter("dayId", dayId)
                .getResultList();
    }

    /**
     * 전달받은 여행 식별자(tripId)에 대응하는 여행의 Schedule들을 모두 얻어옵니다.
     * @param tripId 여행 식별자
     * @return 여행의 일정들
     */
    private List<Schedule> retrieveTemporarySchedules(Long tripId) {
        return em.createQuery("""
                        SELECT s
                        FROM Schedule s
                        WHERE s.trip.id = :tripId and s.day.id IS NULL
                        ORDER BY s.scheduleIndex.value asc
                        """, Schedule.class)
                .setParameter("tripId", tripId)
                .getResultList();
    }
}
