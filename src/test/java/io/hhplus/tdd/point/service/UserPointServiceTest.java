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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertThat(point).isEqualTo(userPointService.getUserPoint(userId));
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

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }
}