package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserPointServiceIntegrateTest {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private UserPointService userPointService;

    @Test
    @DisplayName("포인트가 저장되어 있을때 조회가 가능하다.")
    void getUserPoint() {
        //given
        UserPoint savedUserPoint = userPointRepository.save(1L, 150);
        //when
        UserPoint getUserPoint = userPointService.getUserPoint(savedUserPoint.id());
        //then
        assertThat(savedUserPoint.point()).isEqualTo(getUserPoint.point());
        assertThat(savedUserPoint.id()).isEqualTo(getUserPoint.id());
        assertThat(savedUserPoint.updateMillis()).isEqualTo(getUserPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트가 저장되어 있지 않을때 조회시 포인트는 0으로 조회된다.")
    void getUserPoint1() {
        //given
        long id = 1L;
        long point = 0;
        //when
        UserPoint getUserPoint = userPointService.getUserPoint(id);
        //then
        assertThat(getUserPoint).isEqualTo(point);
    }

    @Test
    @DisplayName("포인트를 충전할수 있다.")
    void chargePoint() {
        //given
        long id = 1L;
        long point = 123L;
        //when
        userPointService.chargePoint(id, point);
        UserPoint savedUserPoint = userPointRepository.findById(id).get();
        //then
        assertThat(savedUserPoint.id()).isEqualTo(id);
        assertThat(savedUserPoint.point()).isEqualTo(point);
    }

    @Test
    @DisplayName("포인트를 충전할때 음수가 들어가면 예외가 발생한다.")
    void chargePointWithMinusPoint() {
        //given
        long id = 1L;
        long point = -1L;
        //when
        //then
        assertThatThrownBy(() -> userPointService.chargePoint(id, point))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 양수여야 합니다.");
    }

    @Test
    @DisplayName("포인트를 충전할때 0이면 예외가 발생한다..")
    void chargePointWithZeroPoint() {
        //given
        long userId = 1L;
        long point = 0L;

        //when
        //then
        assertThatThrownBy(() -> userPointService.chargePoint(userId, point))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 양수여야 합니다.");
    }

    @Test
    @DisplayName("포인트를 충전할 때 PointHistoryTable에 충전 내역을 저장한다.")
    void chargePointSavePointHistory() {
        //given
        long userId = 1L;
        long point = 0L;
        userPointRepository.save(userId, point);

        long chargePoint = 123L;
        UserPoint chargeUserPoint = userPointService.chargePoint(userId, chargePoint);
        //when
        List<PointHistory> list = pointHistoryRepository.findAllByUserId(userId);
        //then
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1)
                .extracting("userId", "type", "amount", "updateMillis")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(1L,TransactionType.CHARGE,123L,chargeUserPoint.updateMillis())
                );
    }

    @Test
    @DisplayName("포인트를 사용할 때 PointHistoryTable에 사용 내역을 저장한다.")
    void usePointSavePointHistory() {
        //given
        long userId = 1L;
        long point = 1000L;
        userPointRepository.save(userId, point);

        long usePoint = 120L;
        UserPoint useUserPoint = userPointService.usePoint(userId, usePoint);
        //when
        List<PointHistory> list = pointHistoryRepository.findAllByUserId(userId);
        //then
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(1)
                .extracting("userId", "type", "amount", "updateMillis")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(1L,TransactionType.USE,880L,useUserPoint.updateMillis())
                );
    }

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }
}