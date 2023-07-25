package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Day 엔티티 또는 Day 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리 구현체입니다.
 */
@Component
@RequiredArgsConstructor
public class DayRepositoryImpl implements DayRepository {

    /**
     * Day 엔티티를 관리하는 Spring Data JPA Repository
     */
    private final JpaDayRepository jpaDayRepository;

    @Override
    public void saveAll(List<Day> days) {
        jpaDayRepository.saveAll(days);
    }

    @Override
    public Optional<Day> findByIdWithTrip(Long dayId) {
        return jpaDayRepository.findByIdWithTrip(dayId);
    }

    @Override
    public int deleteAllByIds(List<Long> dayIds) {
        return jpaDayRepository.deleteAllByIds(dayIds);
    }

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 Day들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    @Override
    public void deleteAllByTripId(Long tripId) {
        jpaDayRepository.deleteAllByTripId(tripId);
    }

    @Override
    public void deleteAllByTripIds(List<Long> tripIds) {
        jpaDayRepository.deleteAllByTripIds(tripIds);
    }
}
