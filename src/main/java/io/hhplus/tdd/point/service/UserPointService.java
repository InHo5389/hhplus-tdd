package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;

    public UserPoint getUserPoint(long id) {
        return userPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    public UserPoint chargePoint(long id, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("포인트는 양수여야 합니다.");
        }
        UserPoint userPoint = userPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));
        return userPointRepository.save(id, userPoint.point() + amount);
    }

    public UserPoint usePoint(long id, long amount) {
        UserPoint userPoint = userPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));

        if (userPoint.point() < amount) {
            throw new RuntimeException("포인트가 부족합니다.");
        }

        if (amount < 0) {
            throw new RuntimeException("사용 금액은 0보다 커야합니다.");
        }
        return userPointRepository.save(id, userPoint.point() - amount);
    }
}
