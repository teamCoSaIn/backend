package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import com.cosain.trilo.support.RestDocsTestSupport;
import jakarta.servlet.http.Cookie;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTest extends RestDocsTestSupport {

    @MockBean
    private AuthService authService;

    private static final String BASE_URL = "/api/auth";

    @Test
    void 접근토큰_재발급_요청() throws Exception{

        given(authService.reissueAccessToken(any())).willReturn("accessToken");

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL +"/reissue")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("authType").type(STRING).description("인증 타입 (Bearer)"),
                                fieldWithPath("accessToken").type(STRING).description("재발급한 접근 토큰")
                        )
                ));
    }

    @Test
    void 접근토큰_재발급_요청시_쿠키가_존재하지_않으면_400_BadRequest_에러를_발생시킨다() throws Exception{
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/reissue"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 재발급_토큰_상태_조회_요청() throws Exception{

        given(authService.createTokenStatus(any())).willReturn(RefreshTokenStatusResponse.from(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info")
                        .cookie(new Cookie("refreshToken", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzd2VldF9zbWVsbEBuYXRlLmNvbSIsImlhdCI6MTY3OTg4MjQ2NiwiZXhwIjoxNjc5ODkzMjY2fQ.v0E4tjveoiOSP2GONUvfTH8-pR_zB5A9w5l5ZNPc4Wk")))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("availability").type(BOOLEAN).description("토큰 사용 가능 여부")
                        )
                ));
    }

    @Test
    void 재발급_토큰_상태_조회_요청시_쿠키가_존재하지_않아도_정상_동작한다() throws Exception{
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL+"/token/refresh-token-info"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 로그아웃_요청() throws Exception{

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                .cookie(new Cookie("refreshToken", "refreshToken"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isOk())
                .andDo(restDocs.document());
    }

    @Test
    @WithMockUser
    void 로그아웃_요청시_쿠키가_존재하지_않으면_400_BadRequest_에러를_발생시킨다() throws Exception{

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그아웃_요청시_인증_헤더가_존재하지_않으면_401_Unauthorized_에러를_발생시킨다() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .cookie(new Cookie("refreshToken", "refreshToken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃_요청시_인증_헤더가_유효하지_않으면_401_Unauthorized_에러를_발생시킨다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/logout")
                        .cookie(new Cookie("refreshToken", "refreshToken"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
                .andExpect(status().isUnauthorized());
    }
}