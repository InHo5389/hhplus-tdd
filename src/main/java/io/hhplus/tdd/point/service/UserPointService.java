package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.request.PointChargeRequest;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public UserPoint getUserPoint(long id) {
        return userPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
    }

    public UserPoint chargePoint(long id, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("포인트는 양수여야 합니다.");
        }

        ReentrantLock userLock = userLocks.computeIfAbsent(id, k -> new ReentrantLock());

        userLock.lock();
        UserPoint userPoint;
        UserPoint savedUserPoint;
        try {
            userPoint = userPointRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));

            savedUserPoint = userPointRepository.save(id, userPoint.point() + amount);
        } finally {
            userLock.unlock();
        }

        pointHistoryRepository.save(savedUserPoint.id(), savedUserPoint.point(), TransactionType.CHARGE, savedUserPoint.updateMillis());
        return savedUserPoint;
    }

    public UserPoint usePoint(long id, long amount) {


        if (amount < 0) {
            throw new RuntimeException("사용 금액은 0보다 커야합니다.");
        }
        ReentrantLock userLock = userLocks.computeIfAbsent(id, k -> new ReentrantLock());

        userLock.lock();
        UserPoint userPoint;
        UserPoint savedUserPoint;
        try {

            userPoint = userPointRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));
            if (userPoint.point() < amount) {
                throw new RuntimeException("포인트가 부족합니다.");
            }

            savedUserPoint = userPointRepository.save(id, userPoint.point() - amount);
        } finally {
            userLock.unlock();
        }

        pointHistoryRepository.save(savedUserPoint.id(), savedUserPoint.point(), TransactionType.USE, savedUserPoint.updateMillis());
        return savedUserPoint;
    }

    /**
     * synchronized{}을 사용한 동시성 제어
     */
//    public UserPoint chargePoint(long id, long amount) {
//        if (amount <= 0) {
//            throw new IllegalArgumentException("포인트는 양수여야 합니다.");
//        }
//        UserPoint userPoint;
//        UserPoint savedUserPoint = null;
//        synchronized (this) {
//            userPoint = userPointRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));
//
//            savedUserPoint = userPointRepository.save(id, userPoint.point() + amount);
//        }
//        pointHistoryRepository.save(savedUserPoint.id(), savedUserPoint.point(), TransactionType.CHARGE, savedUserPoint.updateMillis());
//        return savedUserPoint;
//    }


    /**
     * ReentrantLock을 사용한 동시성 제어
     */
//    public UserPoint chargePoint(long id, long amount) {
//        if (amount <= 0) {
//            throw new IllegalArgumentException("포인트는 양수여야 합니다.");
//        }
//
//        lock.lock();
//        UserPoint savedUserPoint;
//        UserPoint userPoint;
//        try {
//            userPoint = userPointRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("생성된 유저 id가 없습니다."));
//
//            savedUserPoint = userPointRepository.save(id, userPoint.point() + amount);
//        }finally {
//            lock.unlock();
//        }
//
//        pointHistoryRepository.save(savedUserPoint.id(), savedUserPoint.point(), TransactionType.CHARGE, savedUserPoint.updateMillis());
//        return savedUserPoint;
//    }
    public List<PointHistory> getHistory(long id) {
        return pointHistoryRepository.findAllByUserId(id);
    }
}
