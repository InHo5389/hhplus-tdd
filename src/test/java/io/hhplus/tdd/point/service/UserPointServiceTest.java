package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("포인트를 조회한다.")
    void getUserPoint() {
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
    void getUserPointWithOutUserId() {
        //given
        long userId = 1L;
        //when
        //then
        assertThatThrownBy(() -> userPointService.getUserPoint(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 id입니다.");
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다.")
    void chargePoint() {
        //given
        long userId = 1L;
        long point = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        long chargePoint = 50L;
        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));
        when(userPointRepository.save(userId, chargePoint + point))
                .thenReturn(new UserPoint(userId, chargePoint + point, System.currentTimeMillis()));
        //when
        UserPoint savedUserPoint = userPointService.chargePoint(userId, chargePoint);
        //then
        assertThat(userPoint.id()).isEqualTo(savedUserPoint.id());
        assertThat(userPoint.point() + chargePoint).isEqualTo(savedUserPoint.point());
    }

    @Test
    @DisplayName("포인트를 충전할때 음수가 들어가면 예외가 발생한다..")
    void chargePointWithMinusPoint() {
        //given
        long userId = 1L;
        long point = -1L;

        //when
        //then
        assertThatThrownBy(() -> userPointService.chargePoint(userId, point))
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
    @DisplayName("존재하지 않는 유저 ID로 충전하려고 하면 RuntimeException 발생한다")
    void chargePointNonExistingUserId() {
        //given
        long userId = 999L;
        long point = 100L;

        //when
        when(userPointRepository.findById(userId))
                .thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> userPointService.chargePoint(userId, point))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("생성된 유저 id가 없습니다.");
    }

    @Test
    @DisplayName("생성된 id가 있고 포인트가 일정금액 이상 있으면 포인트를 사용할수 있다.")
    void usePoint() {
        //given
        long userId = 1L;
        long point = 1000L;
        long usePoint = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));

        when(userPointRepository.save(userId, usePoint))
                .thenReturn(new UserPoint(userId, point - usePoint, System.currentTimeMillis()));
        //when
        UserPoint usedUserPoint = userPointService.usePoint(userId, usePoint);
        //then
        assertThat(usedUserPoint.point()).isEqualTo(point - usePoint);
    }

    @Test
    @DisplayName("포인트를 사용할 때 포인트가 부족하면 예외가 발생한다.")
    void usePointNotEnoughPoint() {
        //given
        long userId = 1L;
        long point = 100L;
        long usePoint = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));

        //when
        //then
        assertThatThrownBy(() -> userPointService.usePoint(userId, usePoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @Test
    @DisplayName("포인트를 사용할 때 생성된 id가 없으면 예외를 발생한다.")
    void usePointNonExistingUserId() {
        //given
        long userId = 1L;
        long usePoint = 500L;

        when(userPointRepository.findById(userId))
                .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userPointService.usePoint(userId, usePoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("생성된 유저 id가 없습니다.");
    }

    @Test
    @DisplayName("포인트를 충전할 때 PointHistoryTable에 충전 내역을 저장한다.")
    void chargePointSavePointHistory() {
        //given
        long userId = 1L;
        long point = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        long chargePoint = 50L;
        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));
        when(userPointRepository.save(userId, userPoint.point() + chargePoint))
                .thenReturn(new UserPoint(userId, chargePoint + point, System.currentTimeMillis()));
        //when
        UserPoint savedUserPoint = userPointService.chargePoint(userId, chargePoint);
        //then
        verify(pointHistoryRepository, times(1)).save(userId, savedUserPoint.point(), TransactionType.CHARGE, savedUserPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트를 사용할 때 PointHistoryTable에 사용 내역을 저장한다.")
    void usePointSavePointHistory() {
        //given
        long userId = 1L;
        long point = 500L;
        UserPoint userPoint = createUserPoint(userId, point);

        long usePoint = 50L;
        when(userPointRepository.findById(userId))
                .thenReturn(Optional.of(userPoint));
        when(userPointRepository.save(userId, userPoint.point() - usePoint))
                .thenReturn(new UserPoint(userId, userPoint.point() - usePoint, System.currentTimeMillis()));
        //when
        UserPoint savedUserPoint = userPointService.usePoint(userId, usePoint);
        //then
        verify(pointHistoryRepository, times(1)).save(userId, savedUserPoint.point(), TransactionType.USE, savedUserPoint.updateMillis());
    }

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }
}