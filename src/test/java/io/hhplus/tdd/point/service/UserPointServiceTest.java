package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("포인트를 조회한다.")
    void getUserPoint(){
        //given
        long userId = 2L;
        long point = 500L;
        UserPoint userPoint = createUserPoint(userId, point);
        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));
        //when
        //then
        assertThat(point).isEqualTo(userPointService.getUserPoint(userId).point());
    }

    @Test
    @DisplayName("포인트를 조회할 때 id값이 없으면 예외가 발생한다.")
    void getUserPointWithOutUserId(){
        //given
        long userId = 1L;
        //when
        //then
        assertThatThrownBy(()->userPointService.getUserPoint(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 id입니다.");
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다.")
    void chargePoint(){
        //given
        long userId = 1L;
        long point = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        when(userPointRepository.save(userId, point))
                .thenReturn(userPoint);
        //when
        UserPoint savedUserPoint = userPointService.chargePoint(userId,point);
        //then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(point);
    }

    @Test
    @DisplayName("포인트를 충전할때 음수가 들어가면 예외가 발생한다..")
    void chargePointWithMinusPoint(){
        //given
        long userId = 1L;
        long point = -1L;

        //when
        //then
        assertThatThrownBy(()->userPointService.chargePoint(userId,point))
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