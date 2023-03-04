package ru.practicum.mainservice;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomUtils;
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
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MainServiceApplicationTests {
    private static final String USERS = "/admin/users";
    private static final String ADMIN_CATEGORY = "/admin/categories";
    private static final String PRIVATE_EVENT = "/users/{userId}/events";


    @Autowired
    private StatisticClient client;

    @BeforeAll
    private static void run() {
        SpringApplication.run(MainServiceApplication.class);
    }

    private void createUser() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\": \"ivan.petrov" + RandomUtils.nextLong() + RandomUtils.nextInt() +
                        "@practicummail.ru\",\n" +
                        "  \"name\": \"Иван Петров\"\n" +
                        "}")
                .when().post(USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    private void createCategory() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Концерты" + RandomUtils.nextInt() +
                        "  \"\n" +
                        "}")
                .when().post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    private void createEvent() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                        "  \"category\": 1,\n" +
                        "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                        "  \"eventDate\": \"2023-12-31 15:10:05\",\n" +
                        "  \"location\": {\n" +
                        "    \"lat\": 55.754167,\n" +
                        "    \"lon\": 37.62\n" +
                        "  },\n" +
                        "  \"title\": \"Сплав на байдарках\" \n" +
                        "}")
                .pathParam("userId", 1L)
                .when().post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CREATED.value());
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
    void createUser_whenRequestValidAndUserIsNew_thanReturn201AndTrimmedValues() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                        "  \"name\": \" Иван Петров   \"\n" +
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
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                        "  \"name\": \" Иван Петров   \"\n" +
                        "}")
                .when().post(USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

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
    void getUsers_whenNotExistUserByIds_thanReturnEmptyList() {
        createUser();

        given()
                .queryParam("ids", "2")
                .when().get(USERS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", emptyIterable());
    }

    @Test
    void getUsers_whenUsePagingParams_thanReturnUsersListOnPages() {
        for (int i = 0; i < 11; i++) {
            createUser();
            i++;
        }

        given()
                .queryParam("from", 2)
                .queryParam("size", 3)
                .when().get(USERS)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(3))
                .body("id", is(List.of(3, 4, 5)))
                .body("name", is(List.of("Иван Петров", "Иван Петров", "Иван Петров")))
                .body("email", notNullValue());
    }

    @Test
    void deleteUser_whenExistUserAsInitiatorAndParticipation_thren___() {
        //проверить поведение связанных сущностей при удалении юзера
        //если он инициатор события - делит каскад
    }

    @Test
    void deleteUser_whenUserDoesNotExist_thenReturn() {
        given()
                .pathParam("userId", 111)
                .when().delete(USERS + "/{userId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", is("NOT_FOUND"))
                .body("reason", is("The required object was not found."))
                .body("message", containsString("User with id=111 was not found"))
                .body("timestamp", notNullValue());
    }

    @Test
    void createCategory_whenNameIsNotUnique_thanReturn409() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Концерты  \"\n" +
                        "}")
                .when().post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("name", is("Концерты"));
    }

    @Test
    void updateCategory_whenNameIsNotUnique_thanReturn409() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Концерты  \"\n" +
                        "}")
                .when().post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Постановки  \"\n" +
                        "}")
                .when().post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Концерты  \"\n" +
                        "}")
                .pathParam("catId", 2)
                .when().patch(ADMIN_CATEGORY + "/{catId}")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"))
                .body("reason", is("Integrity constraint has been violated."))
                .body("message", is("could not execute statement; SQL [n/a]; constraint [null]; nested exception " +
                        "is org.hibernate.exception.ConstraintViolationException: could not execute statement"))
                .body("timestamp", notNullValue());
    }

    @Test
    void updateCategory_whenRequestValid_thanReturn200() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"name\": \"  Концерты  \"\n" +
                        "}")
                .when().post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .pathParam("catId", 1)
                .body("{\n" +
                        "  \"name\": \" Концерты NEW \"\n" +
                        "}")
                .when().patch(ADMIN_CATEGORY + "/{catId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("name", is("Концерты NEW"));
    }

    @Test
    void createEvent_whenRequestValidWithOnlyRequired_thanReturn201AndDefaultParams() {
        createUser();
        createCategory();

        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                        "  \"category\": 1,\n" +
                        "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                        "  \"eventDate\": \"2023-12-31 15:10:05\",\n" +
                        "  \"location\": {\n" +
                        "    \"lat\": 55.754167,\n" +
                        "    \"lon\": 37.62\n" +
                        "  },\n" +
                        "  \"title\": \"Сплав на байдарках\" \n" +
                        "}")
                .pathParam("userId", 1L)
                .when().post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("annotation", is("Сплав на байдарках похож на полет."))
                .body("category.id", is(1))
                .body("category.name", containsString("Концерты"))
                //   .body("paid", is(true)) bugs
                .body("createdOn", notNullValue())
                .body("description", is("Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления."))
                .body("eventDate", is("2023-12-31 15:10:05"))
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("initiator.name", is("Иван Петров"))
                // .body("location.lat", closeTo(new BigDecimal("55.754167"), new BigDecimal("0.0000001")))
                //    .body("location.lon", is(new BigDecimal("37.62")))
                //   .body("participantLimit", is(0))
                .body("publishedOn", nullValue())
                //  .body("requestModeration", is(true))
                .body("title", is("Сплав на байдарках"))
                // todo  .body("views", is())
                //   .body("confirmedRequests", is())
                .body("state", is("PENDING"));

    }

    @Test
    void createEvent_whenEventDateIsEarlyThan2HoursFromNow_thanReturn409() {
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                        "  \"category\": 1,\n" +
                        "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                        "  \"eventDate\": \"2020-12-31 15:10:05\",\n" +
                        "  \"location\": {\n" +
                        "    \"lat\": 55.754167,\n" +
                        "    \"lon\": 37.62\n" +
                        "  },\n" +
                        "  \"title\": \"Сплав на байдарках\" \n" +
                        "}")
                .pathParam("userId", 1L)
                .when().post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"))
                .body("reason", is("For the requested operation the conditions are not met."))
                .body("message", containsString("Field: eventDate. Error: должно содержать дату, " +
                        "которая еще не наступила. Value: 2020-12-31T15:10:05"));
    }

    @Test
    void updateEvent_whenStatusIsWaitingPublication_return200() {
        createUser();
        createCategory();
        createCategory();
        createEvent();

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .pathParam("eventId", 1)
                .body("{\n" +
                        "  \"annotation\": \"Сап прогулки по рекам и каналам – это возможность увидеть Практикбург с другого ракурса\",\n" +
                        "  \"category\": 2,\n" +
                        "  \"description\": \"От английского SUP - Stand Up Paddle — \\\"стоя на доске с веслом\\\", гавайская разновидность сёрфинга, в котором серфер, стоя на доске, катается на волнах и при этом гребет веслом, а не руками, как в классическом серфинге.\",\n" +
                        "  \"eventDate\": \"2024-10-11 23:10:05\",\n" +
                        "  \"location\": {\n" +
                        "    \"lat\": 24.754167,\n" +
                        "    \"lon\": 24.62\n" +
                        "  },\n" +
                        "  \"paid\": true,\n" +
                        "  \"participantLimit\": 24,\n" +
                        "  \"requestModeration\": false,\n" +
                        "  \"stateAction\": \"CANCEL_REVIEW\",\n" +
                        "  \"title\": \"Сап прогулки по рекам и каналам\"\n" +
                        "}")
                .when().patch(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"));

    }

    @Test
    void updateEvent_whenStatusIsCanceled_return200() {

    }

    @Test
    void updateEvent_whenStatusIsPublished_return409() {

    }
}