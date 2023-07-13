package com.cosain.trilo.trip.domain.entity;

import com.cosain.trilo.trip.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@ToString(of = {"id", "scheduleTitle", "scheduleContent", "scheduleTime", "place", "scheduleIndex"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules")
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    private Day day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Embedded
    private ScheduleTitle scheduleTitle;

    @Embedded
    private ScheduleContent scheduleContent;

    @Embedded
    private ScheduleTime scheduleTime;

    @Embedded
    private Place place;

    @Embedded
    private ScheduleIndex scheduleIndex;

    static Schedule create(Day day, Trip trip, ScheduleTitle scheduleTitle, Place place, ScheduleIndex scheduleIndex) {
        return Schedule.builder()
                .day(day)
                .trip(trip)
                .scheduleTitle(scheduleTitle)
                .scheduleContent(ScheduleContent.defaultContent())
                .place(place)
                .scheduleIndex(scheduleIndex)
                .scheduleTime(ScheduleTime.defaultTime())
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Schedule(Long id, Day day, Trip trip, ScheduleTitle scheduleTitle, ScheduleContent scheduleContent, ScheduleTime scheduleTime, Place place, ScheduleIndex scheduleIndex) {
        this.id = id;
        this.day = day;
        this.trip = trip;
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent == null ? ScheduleContent.defaultContent() : scheduleContent;
        this.scheduleTime = scheduleTime == null ? ScheduleTime.defaultTime() : scheduleTime;
        this.place = place;
        this.scheduleIndex = scheduleIndex;
    }

    public void changeTitle(ScheduleTitle scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public void changeContent(ScheduleContent scheduleContent) {
        this.scheduleContent = scheduleContent;
    }

    public void changeTime(ScheduleTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * 일정의 day와 ScheduleIndex를 변경합니다.
     * 기존의 day 또는 임시보관함이 있으면 해당 Day와 관계를 끊고 새로운 day 또는 임시보관함과 관계를 맺습니다.
     *
     * @param changeDay
     * @param changeScheduleIndex
     */
    void changePosition(Day changeDay, ScheduleIndex changeScheduleIndex) {
        detachFromDayOrTemporaryStorage();
        this.day = changeDay;
        this.scheduleIndex = changeScheduleIndex;
    }

    /**
     * 자신이 속했던 Day 또는, 임시보관함에서 Schedule 자신을 끊어냅니다.
     */
    private void detachFromDayOrTemporaryStorage() {
        if (day == null) {
            trip.detachScheduleFromTemporaryStorage(this);
            return;
        }
        day.detachSchedule(this);
    }
}
