package com.cosain.trilo.integration.user;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.cosain.trilo.user.presentation.dto.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("사용자 관련 기능 통합 테스트")
public class UserIntegrationTest extends IntegrationTest {
    private final String BASE_URL = "/api/users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private Clock clock;

    @Value("${cloud.aws.s3.bucket-path}")
    private String myPageBaseUrl;

    @Nested
    class 회원_프로필_조회{
        @Test
        void 회원_프로필_조회_성공() throws Exception{
            // given
            User user = setupMockKakaoUser();

            log.info("user = {}", user);

            flushAndClear();
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.nickName").value(user.getNickName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.profileImageURL").value(user.getProfileImageURL()))
                    .andExpect(jsonPath("$.role").value(user.getRole().name()));
        }

        @Test
        void 회원_프로필_조회_시_다른_회원의_프로필을_조회할_경우_403_응답() throws Exception{
            // given
            User requestUser = setupMockGoogleUser();
            User targetUser = setupMockKakaoUser();

            log.info("requestUser = {}", requestUser);
            log.info("targetUser = {}", targetUser);

            flushAndClear();
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/profile", targetUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("user-0002"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }

    @Nested
    class 회원_탈퇴{
        @Test
        void 회원_탈퇴_성공() throws Exception{
            // given
            User user = setupMockKakaoUser();
            log.info("User = {}", user);

            flushAndClear();
            // when
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", user.getId())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user)))
                    .andExpect(status().isNoContent());

            // then
            User findUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(findUser.isDeleted()).isTrue();
        }

        @Test
        void 회원_탈퇴_시_본인이_아닌_회원의_탈퇴_요청을_하는_경우_403_응답() throws Exception{
            // given
            User requestUser = setupMockKakaoUser();
            User targetUser = setupMockGoogleUser();
            log.info("requestUser = {}", requestUser);
            log.info("targetUser = {}", targetUser);

            flushAndClear();
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{userId}", targetUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("user-0004"))
                    .andExpect(jsonPath("$.errorMessage").exists())
                    .andExpect(jsonPath("$.errorDetail").exists());
        }
    }

    @Nested
    class 마이페이지_조회{
        @Test
        void 성공() throws Exception{
            // given
            User user = setupMockKakaoUser();
            LocalDate today = LocalDate.now(clock);
            int terminatedTripCnt = 3;
            int unTerminatedTripCnt = 5;
            int totalTripCnt = terminatedTripCnt + unTerminatedTripCnt;

            createTrip(user, today.minusDays(5), today.minusDays(3), terminatedTripCnt);
            createTrip(user, today.plusDays(3), today.plusDays(5), unTerminatedTripCnt);

            flushAndClear();
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{userId}/my-page", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.nickName").value(user.getNickName()))
                    .andExpect(jsonPath("$.imageURL").value(myPageBaseUrl.concat(user.getMyPageImage().getFileName())))
                    .andExpect(jsonPath("$.tripStatistics.totalTripCnt").value(totalTripCnt))
                    .andExpect(jsonPath("$.tripStatistics.terminatedTripCnt").value(terminatedTripCnt));
        }

        private void createTrip(User user, LocalDate startDate, LocalDate endDate, int cnt){
            for(int i = 0; i<cnt; i++){
                Trip trip = TripFixture.decided_nullId(user.getId(), startDate, endDate);
                tripRepository.save(trip);
            }
        }
    }

    @Nested
    class 회원_정보_수정{
        @Test
        void 성공() throws Exception{
            // given
            String nickName = "변경할 닉네임";
            User user = setupMockKakaoUser();
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(nickName);

            flushAndClear();
            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.patch(BASE_URL + "/{userId}", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                            .content(createRequestJson(userUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            flushAndClear();
            // then
            resultActions
                    .andExpect(MockMvcResultMatchers.status().isOk());

            User findUser = userRepository.findById(user.getId()).orElseThrow(IllegalStateException::new);
            log.info("findUser = {}" , findUser);
            assertThat(findUser.getNickName()).isEqualTo(nickName);

        }
    }
}
