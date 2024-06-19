package io.hhplus.tdd.point.contoller;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.request.PointChargeRequest;
import io.hhplus.tdd.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointService userPointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {

        UserPoint userPoint = userPointService.getUserPoint(id);
        return new UserPoint(userPoint.id(), userPoint.point(), userPoint.updateMillis());
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody PointChargeRequest request
    ) {
        UserPoint userPoint = userPointService.chargePoint(id, request.getAmount());
        return new UserPoint(userPoint.id(), userPoint.point(), userPoint.updateMillis());
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        if (amount <= 0 ){
            throw new IllegalArgumentException("포인트는 양수 값으로만 사용할 수 있습니다.");
        }
        return userPointService.usePoint(id,amount);
    }
}
