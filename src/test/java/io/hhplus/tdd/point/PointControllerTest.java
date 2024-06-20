package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.contoller.PointController;
import io.hhplus.tdd.point.dto.request.PointChargeRequest;
import io.hhplus.tdd.point.service.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @MockBean
    private UserPointService userPointService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("특정 유저의 포인트를 조회한다.")
    void point() throws Exception {
        //given
        long userId = 5;
        long point = 500L;
        long currentTimeMillis = System.currentTimeMillis();
        UserPoint userPoint = new UserPoint(userId, point, currentTimeMillis);

        //stub
        when(userPointService.getUserPoint(userId)).thenReturn(userPoint);

        //when
        //then
        mockMvc.perform(
                        get("/point/{id}", userId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userPoint.id()))
                .andExpect(jsonPath("$.point").value(userPoint.point()));
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전한다.")
    void charge() throws Exception {
        //given
        long userId = 5L;
        long point = 500L;
        PointChargeRequest request = new PointChargeRequest(point);

        long currentTimeMillis = System.currentTimeMillis();
        UserPoint userPoint = new UserPoint(userId, point, currentTimeMillis);

        //stub
        when(userPointService.chargePoint(userId, point))
                .thenReturn(userPoint);

        //when
        //then
        mockMvc.perform(
                        patch("/point/{id}/charge", userId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userPoint.id()))
                .andExpect(jsonPath("$.point").value(userPoint.point()));
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전할때 요청 body값이 없으면 400에러가 발생한다..")
    void chargeWithOutRequestBody() throws Exception {
        //given
        long userId = 5L;
        //when
        //then
        mockMvc.perform(
                        patch("/point/{id}/charge", userId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용한다.")
    void use() throws Exception {
        //given
        long userId = 5L;
        long point = 500L;

        long currentTimeMillis = System.currentTimeMillis();
        UserPoint userPoint = new UserPoint(userId, point, currentTimeMillis);

        //stub
        when(userPointService.usePoint(userId, point))
                .thenReturn(userPoint);

        //when
        //then
        mockMvc.perform(
                        patch("/point/{id}/use", userId)
                                .content(String.valueOf(point))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userPoint.id()))
                .andExpect(jsonPath("$.point").value(userPoint.point()));
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용할때 음수값이 오면 예외를 리턴한다.")
    void useWithNonPositive() throws Exception {
        //given
        long userId = 5L;
        long amount = -1L;

        //when
        //then
        mockMvc.perform(
                        patch("/point/{id}/use", userId)
                                .content(String.valueOf(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("포인트는 양수 값으로만 사용할 수 있습니다."));
    }

    @Test
    @DisplayName("특정 유저의 history내역을 조회할 수 있다.")
    void history() throws Exception {
        //given
        long userId = 1L;
        long updateMillis = System.currentTimeMillis();
        PointHistory pointHistory1 = createPointHistory(1L, userId, 50L, TransactionType.CHARGE, updateMillis);
        PointHistory pointHistory2 = createPointHistory(2L, userId, 150L, TransactionType.USE, updateMillis);
        PointHistory pointHistory3 = createPointHistory(3L, userId, 500L, TransactionType.CHARGE, updateMillis);
        List<PointHistory> pointHistoryList = List.of(pointHistory1, pointHistory2, pointHistory3);

        //when
        when(userPointService.getHistory(userId))
                .thenReturn(pointHistoryList);
        //then
        mockMvc.perform(
                get("/point/{id}/histories",userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[2].userId").value(userId))
                .andExpect(jsonPath("$[0].amount").value(50L))
                .andExpect(jsonPath("$[1].amount").value(150L))
                .andExpect(jsonPath("$[2].amount").value(500L))
        ;
    }

    @Test
    @DisplayName("특정 유저의 history내역을 조회할 때 history가 비어있으면 빈 배열을 반환한다..")
    void historyEmpty() throws Exception {
        //given
        long userId = 1L;
        List<PointHistory> pointHistoryList = List.of();

        //when
        when(userPointService.getHistory(userId))
                .thenReturn(pointHistoryList);
        //then
        mockMvc.perform(
                get("/point/{id}/histories",userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    private PointHistory createPointHistory(long id, long userId,long amount,TransactionType type,long updateMillis){
        return PointHistory.builder()
                .id(id)
                .userId(userId)
                .amount(amount)
                .type(type)
                .updateMillis(updateMillis)
                .build();
    }
}