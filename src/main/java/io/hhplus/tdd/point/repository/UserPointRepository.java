package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;

import java.util.Optional;

public interface UserPointRepository {

    Optional<UserPoint> findById(long id);
    UserPoint save(UserPoint userPoint);
}
