package ru.practicum.mainservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.statisticclient.StatisticClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class MainServiceApplicationTests {
    @Autowired
    private StatisticClient client;

    @Test
    void contextLoads() {
    }

    @Test
    @Disabled("Проверка клиента статистики POST. Запускается вручную, подняв сервер статистики")
    void addHit_whenRequestValid_return201() throws IOException, InterruptedException {
        HttpResponse<String> response = client.addHit("/events/3",
            "192.163.0.1", LocalDateTime.now());

        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    @Disabled("Проверка клиента статистики GET. Запускается вручную, подняв сервер статистики")
    void getStatistics_whenAllParametersValid_thenReturn200() throws IOException, InterruptedException {
        HttpResponse<String> response = client.getStatistics(
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now(),
            List.of("url/1", "/events/3"),
            true
        );

        Assertions.assertEquals(200, response.statusCode());
    }
}
