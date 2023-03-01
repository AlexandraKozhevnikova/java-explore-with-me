package ru.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class StatisticServerApplicationTests {
    private RestTemplate restTemplate;

    @BeforeAll
    static void runApplication() {
        SpringApplication.run(StatisticServerApplication.class);
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createHit_whenRequestValid_return201() throws URISyntaxException {
        ResponseEntity<?> response = restTemplate.postForEntity(
                new URI("http://localhost:9090/hit"),
                "{\n" +
                        "  \"app\": \"ewm-main-service\",\n" +
                        "  \"uri\": \"/events/1\",\n" +
                        "  \"ip\": \"192.163.0.1\",\n" +
                        "  \"timestamp\": \"2022-09-06 11:00:23\"\n" +
                        "}",
                Object.class
        );
        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertFalse(response.hasBody());
    }

    @Test
    void getStatistics_whenRequestValid_returnListOfHits() {
        Map<String, String> parameters = Map.of(
                "start", "2022-01-06 11:00:23",
                "end", "2032-09-06 11:00:23"
        );
        ResponseEntity<List> response = restTemplate.getForEntity(
                "http://localhost:9090/stats?start={start}&end={end}",
                List.class,
                parameters
        );
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertTrue(response.hasBody());
    }
}
