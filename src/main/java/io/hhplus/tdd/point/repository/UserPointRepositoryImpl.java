package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public Optional<UserPoint> findById(long id) {
        return Optional.of(userPointTable.selectById(id));
    }

    @Override
    public UserPoint save(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public void clear() {
        userPointTable.clear();
        ;
    }
}
