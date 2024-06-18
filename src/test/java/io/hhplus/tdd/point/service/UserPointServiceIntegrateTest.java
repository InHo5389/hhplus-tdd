package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserPointServiceIntegrateTest {

    @Autowired
    private UserPointRepository userPointRepository;

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
        assertThatThrownBy(()->userPointService.chargePoint(id,point))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 양수여야 합니다.");
    }

    @Test
    @DisplayName("포인트를 충전할때 0이면 예외가 발생한다..")
    void chargePointWithZeroPoint(){
        //given
        long userId = 1L;
        long point = 0L;

        //when
        //then
        assertThatThrownBy(()->userPointService.chargePoint(userId,point))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 양수여야 합니다.");
    }

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }
}