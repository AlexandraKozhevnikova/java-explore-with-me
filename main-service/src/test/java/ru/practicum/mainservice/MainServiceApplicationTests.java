package ru.practicum.mainservice;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.statisticclient.StatisticClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MainServiceApplicationTests {
    private static final String USERS = "/admin/users";

    @Autowired
    private StatisticClient client;

    @BeforeAll
    private static void run() {
        SpringApplication.run(MainServiceApplication.class);
    }

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

    @Test
    void createUser_whenRequestValidAndUserIsNew_thanReturn201() {
        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                "  \"name\": \"Иван Петров\"\n" +
                "}")
            .when().post(USERS)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", is(1))
            .body("name", is("Иван Петров"))
            .body("email", is("ivan.petrov@practicummail.ru"));
    }

    @Test
    void createUser_whenUserEmailAlreadyExistInDb_thanReturn409() {
        createUser();

        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                "  \"name\": \"Иван Петров\"\n" +
                "}")
            .when().post(USERS)
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("status", is("CONFLICT"))
            .body("reason", is("Integrity constraint has been violated."))
            .body("message", is("could not execute statement; SQL [n/a]; constraint [null]; nested exception " +
                "is org.hibernate.exception.ConstraintViolationException: could not execute statement"))
            .body("timestamp", notNullValue());
    }

    @Test
    void createUser_whenWithoutBody_thanReturn400WithCustomErrorBody() {
        given()
            .contentType(ContentType.JSON)
            .when().post(USERS)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", is("BAD_REQUEST"))
            .body("reason", is("Incorrectly made request."))
            .body("message", containsString("Required request body is missing"))
            .body("timestamp", notNullValue());
    }

    @Test
    void getUsers_whenNotExistUserByIds_thanReturnEmptyList() throws Exception {
//trim()
    }

    @Test
    void getUsers_whenUsePagingParams_thanReturnUsersListOnPages() throws Exception {
//todo
    }

    private void createUser() {
        given()
            .contentType(ContentType.JSON)
            .body("{\n" +
                "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                "  \"name\": \"Иван Петров\"\n" +
                "}")
            .when().post(USERS)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

}
