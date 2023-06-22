package com.cosain.trilo.unit.trip.presentation.schedule.query.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.query.usecase.ScheduleDetailSearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.presentation.schedule.query.SingleScheduleQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SingleScheduleQueryController.class)
public class SingleScheduleQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private ScheduleDetailSearchUseCase scheduleDetailSearchUseCase;

    private final String BASE_URL = "/api/schedules";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 일정_단건_조회() throws Exception{

        Long scheduleId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleDetail scheduleDetail = new ScheduleDetail(scheduleId, 1L, "제목", "장소 이름", 23.23, 23.23, 1L, "내용", LocalTime.of(15, 0), LocalTime.of(15, 30));
        given(scheduleDetailSearchUseCase.searchScheduleDetail(anyLong())).willReturn(scheduleDetail);

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{scheduleId}", scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("scheduleId").description("조회할 일정 ID")
                        ),
                        responseFields(
                                fieldWithPath("scheduleId").type(NUMBER).description("일정 ID"),
                                fieldWithPath("dayId").type(NUMBER).description("Day ID"),
                                fieldWithPath("title").type(STRING).description("일정 제목"),
                                fieldWithPath("placeName").type(STRING).description("장소 이름"),
                                fieldWithPath("order").type(NUMBER).description("일정 순서"),
                                fieldWithPath("content").type(STRING).description("일정 내용"),
                                subsectionWithPath("coordinate").type(OBJECT).description("장소의 좌표"),
                                subsectionWithPath("scheduleTime").type(OBJECT).description("일정 시간 계획")
                        ),
                        responseFields(
                                beneathPath("coordinate").withSubsectionId("coordinate"),
                                fieldWithPath("latitude").type(NUMBER).description("위도"),
                                fieldWithPath("longitude").type(NUMBER).description("경도")
                        ),
                        responseFields(
                                beneathPath("scheduleTime").withSubsectionId("scheduleTime"),
                                fieldWithPath("startTime").type(STRING).description("일정 시작 시간"),
                                fieldWithPath("endTime").type(STRING).description("일정 종료 시간")
                        )
                ));
    }
}
