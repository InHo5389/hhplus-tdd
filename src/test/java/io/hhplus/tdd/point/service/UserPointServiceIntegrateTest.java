package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserPointServiceIntegrateTest {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private UserPointService userPointService;

    @Test
    @DisplayName("포인트가 저장되어 있을때 조회가 가능하다.")
    void getUserPoint(){
        //given
        UserPoint userPoint = createUserPoint(1L, 150);
        UserPoint savedUserPoint = userPointRepository.save(userPoint);
        //when
        UserPoint getUserPoint = userPointService.getUserPoint(savedUserPoint.id());
        //then
        assertThat(savedUserPoint.point()).isEqualTo(getUserPoint.point());
        assertThat(savedUserPoint.id()).isEqualTo(getUserPoint.id());
        assertThat(savedUserPoint.updateMillis()).isEqualTo(getUserPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트가 저장되어 있지 않을때 조회시 포인트는 0으로 조회된다.")
    void getUserPoint1(){
        //given
        long id = 1L;
        long point = 0;
        //when
        UserPoint getUserPoint = userPointService.getUserPoint(id);
        //then
        assertThat(getUserPoint).isEqualTo(point);
    }

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }
}