package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;

import java.util.List;

public interface PointHistoryRepository {

    List<PointHistory> findAllByUserId(long userId);

    PointHistory save(long userId, long amount, TransactionType type, long updateMillis);

    /**
     * 현재 @Transactional 의 롤백이라던가, clear() 가 이용 불가능하므로
     * 통합 테스트 목적을 위해 해당 기능을 추가 구현합니다.
     */
    void clear();
}
