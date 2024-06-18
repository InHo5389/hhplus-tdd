package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointHistoryRepositoryImplTest {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private UserPointRepository userPointRepository;

    @Test
    @DisplayName("테이블에 id값으로 저장 되어있을때  유저ID로 유저 포인트를 조회한다.")
    void findAllByUserIdWithSavedUserId() {
        //given
        UserPoint userPoint1 = createUserPoint(1L, 100L);
        UserPoint userPoint2 = createUserPoint(2L, 300L);
        UserPoint userPoint3 = createUserPoint(3L, 500L);
        //when
        userPointTable.insertOrUpdate(userPoint1.id(), userPoint1.point());
        userPointTable.insertOrUpdate(userPoint2.id(), userPoint2.point());
        userPointTable.insertOrUpdate(userPoint3.id(), userPoint3.point());
        //then
        assertThat(userPointRepository.findById(1L).get())
                .extracting("id", "point")
                .contains(1L, 100L);
    }

    @Test
    @DisplayName("테이블에 id값으로 저장 되어있지 않을때 id값과 포인트 0으로 리턴한다.")
    void findAllByUserIdWithoutUserId() {
        //given
        //when
        //then
        assertThat(userPointRepository.findById(2L).get())
                .extracting("id", "point")
                .contains(2L, 0L);
    }

    @Test
    @DisplayName("테이블에 id값과 point를 저장한다.")
    void save() {
        //given
        long id = 1L;
        long point = 333L;
        UserPoint userPoint = createUserPoint(id, point);
        //when
        //then
        assertThat(userPointRepository.save(userPoint))
                .extracting("id", "point")
                .contains(id, point);
    }

    @Test
    @DisplayName("테이블에 point가 있을시 수정한다.")
    void update() {
        //given
        UserPoint userPoint = userPointTable.insertOrUpdate(1L, 333L);
        long newId = 2L;
        long newPoint = 500L;
        UserPoint newUserPoint = createUserPoint(newId, newPoint);
        //when
        //then
        assertThat(userPointRepository.save(newUserPoint))
                .extracting("id", "point")
                .contains(newId, newPoint);
    }

    private UserPoint createUserPoint(long id, long point) {
        return UserPoint.builder()
                .id(id)
                .point(point)
                .build();
    }

}