package ru.practicum.mainservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.statisticclient.StatisticClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@SpringBootTest
class MainServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @Disabled("Проверка клиента статистики. Запускается вручную, подняв сервер статистики")
    void clientStatistic() throws IOException, InterruptedException {
        StatisticClient client = new StatisticClient("http://localhost:9090");
        HttpResponse<String> response = client.addHit("ewm-main-service", "/events/3",
            "192.163.0.1", LocalDateTime.now());

        Assertions.assertEquals(201, response.statusCode());
    }

}
