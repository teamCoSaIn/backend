package com.cosain.trilo.unit.trip.presentation.trip.command;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.command.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.TripUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행 수정 API 테스트")
@WebMvcTest(TripUpdateController.class)
class TripUpdateControllerTest extends RestControllerTest {

    @MockBean
    private TripUpdateUseCase tripUpdateUseCase;

    @MockBean
    private TripUpdateCommandFactory tripUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTrip_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String rawTitle = "변경할 제목";
        LocalDate startDate = LocalDate.of(2023,5,10);
        LocalDate endDate = LocalDate.of(2023,5,15);


        TripUpdateRequest request = new TripUpdateRequest(rawTitle, startDate, endDate);

        given(tripUpdateCommandFactory.createCommand(eq(rawTitle), eq(startDate), eq(endDate)))
                .willReturn(new TripUpdateCommand(TripTitle.of(rawTitle), TripPeriod.of(startDate, endDate)));
        willDoNothing().given(tripUpdateUseCase).updateTrip(eq(tripId), any(), any(TripUpdateCommand.class));


        mockMvc.perform(put("/api/trips/"+ tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedTripId").value(1L));

        verify(tripUpdateUseCase).updateTrip(eq(tripId), any(), any(TripUpdateCommand.class));
        verify(tripUpdateCommandFactory).createCommand(eq(rawTitle), eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateTrip_with_unauthorizedUser() throws Exception {
        mockMvc.perform(put("/api/trips"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }


    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTrip_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        String emptyContent = "";

        mockMvc.perform(put("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(emptyContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTrip_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidContent = """
                {
                    "title": 괄호로 감싸지 않은 제목,
                    "startDate": "2023-03-01",
                    "endDate": "2023-03-02"
                }
                """;

        mockMvc.perform(put("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("타입이 올바르지 않은 요청 데이터 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateTrip_with_invalidType() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidTypeContent = """
                {
                    "title": "제목",
                    "startDate": "2023-03-01",
                    "endDate": "날짜형식이 아닌 문자열"
                }
                """;

        mockMvc.perform(put("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(invalidTypeContent)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
