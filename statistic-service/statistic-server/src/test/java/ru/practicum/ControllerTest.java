package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
public class ControllerTest {
    @MockBean
    private StatsService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void getStatistics_whenValidRequest_return200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/stats?start={start}&end={end}", "2022-09-06 11:00:23", "2032-09-06 11:00:23"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getStatistics_whenWithoutParams_return400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/stats"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
