package com.cosain.trilo.unit.trip.query.application;

import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import com.cosain.trilo.trip.query.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.query.application.service.TripListSearchService;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import com.cosain.trilo.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TripListSearchServiceTest {
    @InjectMocks
    private TripListSearchService tripListSearchService;

    @Mock
    private TripQueryRepository tripQueryRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("정상 호출 시에 호출 및 반환 테스트")
    void searchTripDetailsTest(){
        // given
        Long tripperId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TripDetail tripDetail1 = new TripDetail(1L, tripperId, "여행 1", TripStatus.DECIDED, LocalDate.now(), LocalDate.now());
        TripDetail tripDetail2 = new TripDetail(2L, tripperId, "여행 2", TripStatus.UNDECIDED, LocalDate.now(), LocalDate.now());
        Slice<TripDetail> tripDetailSlice = new PageImpl<>(List.of(tripDetail1, tripDetail2), pageable, 2L);

        given(userRepository.findById(eq(1L))).willReturn(Optional.of(KAKAO_MEMBER.create()));
        given(tripQueryRepository.findTripDetailListByTripperId(tripperId, pageable)).willReturn(tripDetailSlice);

        // when
        TripPageResponse tripPageResponse = tripListSearchService.searchTripDetails(tripperId, pageable);

        // then
        assertThat(tripPageResponse).isNotNull();
        assertThat(tripPageResponse.getTrips()).hasSize(2);
        assertThat(tripPageResponse.getTrips().get(0).getTitle()).isEqualTo(tripDetail1.getTitle());
        assertThat(tripPageResponse.getTrips().get(1).getTitle()).isEqualTo(tripDetail2.getTitle());
        assertThat(tripPageResponse.isHasNext()).isFalse();

    }

    @Test
    @DisplayName("tripperId에 해당하는 사용자가 존재하지 않으면 TripperNotFoundException 에러를 반환한다.")
    void when_the_user_is_not_exist_that_coincide_with_tripper_id_it_will_throws_TripperNotFoundException(){
        // given
        Long tripperId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        given(userRepository.findById(tripperId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripListSearchService.searchTripDetails(tripperId, pageable)).isInstanceOf(TripperNotFoundException.class);

    }

}
