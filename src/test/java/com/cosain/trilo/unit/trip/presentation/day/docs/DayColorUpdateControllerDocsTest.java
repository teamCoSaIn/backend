package com.cosain.trilo.unit.trip.presentation.day.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.day.service.day_color_update.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.service.day_color_update.DayColorUpdateService;
import com.cosain.trilo.trip.presentation.day.DayColorUpdateController;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DayColorUpdateController.class)
@DisplayName("Day 색상 수정 API DOCS 테스트")
public class DayColorUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private DayColorUpdateService dayColorUpdateService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 DayColor 수정 요청 -> 성공")
    public void dayColorUpdateDocsTest() throws Exception {
        // given
        Long dayId = 1L;
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);

        String rawColorName = "RED";
        DayColorUpdateRequest request = new DayColorUpdateRequest(rawColorName);

        var command = DayColorUpdateCommand.of(dayId, requestTripperId, rawColorName);

        willDoNothing()
                .given(dayColorUpdateService)
                .updateDayColor(eq(command));


        // when
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(createJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(dayId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("dayId")
                                        .description("Day의 식별자(id)")
                        ),
                        requestFields(
                                fieldWithPath("colorName")
                                        .type(STRING)
                                        .description("변경할 색상 이름")
                                        .attributes(key("constraints").value("null일 수 없으며, 아래의 설명을 참고하여 가능한 색상 이름을 전달해주세요. (대소문자 구분 없음)"))
                        ),
                        responseFields(
                                fieldWithPath("dayId")
                                        .type(NUMBER)
                                        .description("Day의 식별자(id)")
                        )
                ));

        verify(dayColorUpdateService, times(1)).updateDayColor(eq(command));
    }

}
