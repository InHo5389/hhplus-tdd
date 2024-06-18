package io.hhplus.tdd.point;

import io.hhplus.tdd.point.contoller.PointController;
import io.hhplus.tdd.point.service.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPointService userPointService;

    @Test
    @DisplayName("특정 유저의 포인트를 조회한다.")
    void point() throws Exception {
        //given
        long userId = 5;
        long point = 500L;
        LocalDateTime now = LocalDateTime.now();
        long epochMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        UserPoint userPoint = new UserPoint(userId, point, epochMillis);
        //when
        //then
        mockMvc.perform(
                get("/point/{id}",userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userPoint.id()))
                .andExpect(jsonPath("$.point").value(userPoint.point()));
    }
}