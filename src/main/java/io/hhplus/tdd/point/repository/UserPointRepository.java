package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;

import java.util.Optional;

public interface UserPointRepository {

    Optional<UserPoint> findById(long id);

    UserPoint save(long id, long amount);

    /**
     * 현재 @Transactional 의 롤백이라던가, clear() 가 이용 불가능하므로
     * 통합 테스트 목적을 위해 해당 기능을 추가 구현합니다.
     */
    void clear();
}
