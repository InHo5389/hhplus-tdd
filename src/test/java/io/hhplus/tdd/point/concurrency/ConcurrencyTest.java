package io.hhplus.tdd.point.concurrency;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.UserPointService;
import net.bytebuddy.build.ToStringPlugin;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

// 포인트 충전,사용에 대하여 진짜 동시성 문제가 일어나나 확인하는 테스트 입니다.
@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Test
    @DisplayName("")
    void concurrentRequest() throws InterruptedException {
        //given
        long userId  = 1L;
        long point = 5L;
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(()->{
                try {
                    userPointService.chargePoint(userId,point);
                }finally {
                    latch.countDown();;
                }
            });
        }

        latch.await();
        //when
        UserPoint userPoint = userPointRepository.findById(userId).get();
        //then
        assertThat(userPoint.point()).isEqualTo(500L);
    }
}
