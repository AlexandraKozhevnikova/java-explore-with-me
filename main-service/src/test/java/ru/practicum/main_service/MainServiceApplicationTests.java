package ru.practicum.main_service;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
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
    private static final String ADMIN_EVENT = "/admin/events/{eventId}";


    @BeforeAll
    private static void run() {
        Properties props = System.getProperties();
        props.setProperty("STATISTIC_SERVER_URL", "http://localhost:9090");
        SpringApplication.run(MainServiceApplication.class);
    }

    private void createUser() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"email\": \"ivan.petrov" + RandomUtils.nextLong() + RandomUtils.nextInt() + "@practicummail.ru\",\n" + "  \"name\": \"Иван Петров\"\n" + "}")
                .when()
                .post(USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    private void createCategory() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Концерты" + RandomUtils.nextInt() + "  \"\n" + "}")
                .when()
                .post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    private void createEvent() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" + "  \"category\": 1,\n" + "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" + "  \"eventDate\": \"2023-12-31 15:10:05\",\n" + "  \"location\": {\n" + "    \"lat\": 55.754167,\n" + "    \"lon\": 37.62\n" + "  },\n" + "  \"title\": \"Сплав на байдарках\" \n" + "}")
                .pathParam("userId", 1L)
                .when()
                .post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }


    @Test
    void contextLoads() {
    }

    @Test
    void createUser_whenRequestValidAndUserIsNew_thanReturn201AndTrimmedValues() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"email\": \"ivan.petrov@practicummail.ru\",\n" + "  \"name\": \" Иван Петров   \"\n" + "}")
                .when()
                .post(USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("name", is("Иван Петров"))
                .body("email", is("ivan.petrov@practicummail.ru"));
    }

    @Test
    void createUser_whenUserEmailAlreadyExistInDb_thanReturn409() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"email\": \"ivan.petrov@practicummail.ru\",\n" + "  \"name\": \" Иван Петров   \"\n" + "}")
                .when()
                .post(USERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"email\": \"ivan.petrov@practicummail.ru\",\n" + "  \"name\": \"Иван Петров\"\n" + "}")
                .when()
                .post(USERS)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"))
                .body("reason", is("Integrity constraint has been violated."))
                .body("message", is("could not execute statement; SQL [n/a]; constraint [null]; nested exception " + "is org.hibernate.exception.ConstraintViolationException: could not execute statement"))
                .body("timestamp", notNullValue());
    }

    @Test
    void createUser_whenWithoutBody_thanReturn400WithCustomErrorBody() {
        given().contentType(ContentType.JSON)
                .when()
                .post(USERS)
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

        given().queryParam("ids", "2")
                .when()
                .get(USERS)
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

        given().queryParam("from", 2)
                .queryParam("size", 3)
                .when()
                .get(USERS)
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
        given().pathParam("userId", 111)
                .when()
                .delete(USERS + "/{userId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", is("NOT_FOUND"))
                .body("reason", is("The required object was not found."))
                .body("message", containsString("User with id=111 was not found"))
                .body("timestamp", notNullValue());
    }

    @Test
    void createCategory_whenNameIsNotUnique_thanReturn409() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Концерты  \"\n" + "}")
                .when()
                .post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("name", is("Концерты"));
    }

    @Test
    void updateCategory_whenNameIsNotUnique_thanReturn409() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Концерты  \"\n" + "}")
                .when()
                .post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Постановки  \"\n" + "}")
                .when()
                .post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Концерты  \"\n" + "}")
                .pathParam("catId", 2)
                .when()
                .patch(ADMIN_CATEGORY + "/{catId}")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"))
                .body("reason", is("Integrity constraint has been violated."))
                .body("message", is("could not execute statement; SQL [n/a]; constraint [null]; nested exception " + "is org.hibernate.exception.ConstraintViolationException: could not execute statement"))
                .body("timestamp", notNullValue());
    }

    @Test
    void updateCategory_whenRequestValid_thanReturn200() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"name\": \"  Концерты  \"\n" + "}")
                .when()
                .post(ADMIN_CATEGORY)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON)
                .pathParam("catId", 1)
                .body("{\n" + "  \"name\": \" Концерты NEW \"\n" + "}")
                .when()
                .patch(ADMIN_CATEGORY + "/{catId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("name", is("Концерты NEW"));
    }

    @Test
    void createEvent_whenRequestValidWithOnlyRequired_thanReturn201AndDefaultParams() {
        createUser();
        createCategory();

        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" + "  \"category\": 1,\n" + "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" + "  \"eventDate\": \"2023-12-31 15:10:05\",\n" + "  \"location\": {\n" + "    \"lat\": 55.754167,\n" + "    \"lon\": 37.62\n" + "  },\n" + "  \"title\": \"Сплав на байдарках\" \n" + "}")
                .pathParam("userId", 1L)
                .when()
                .post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("annotation", is("Сплав на байдарках похож на полет."))
                .body("category.id", is(1))
                .body("category.name", containsString("Концерты"))
                .body("paid", is(false))
                .body("createdOn", notNullValue())
                .body("description", is("Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления."))
                .body("eventDate", is("2023-12-31 15:10:05"))
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("initiator.name", is("Иван Петров"))
                .body("location.lat", equalTo(Float.parseFloat("55.754167")))
                .body("location.lon", equalTo(Float.parseFloat("37.62")))
                .body("participantLimit", is(0))
                .body("publishedOn", nullValue())
                .body("requestModeration", is(true))
                .body("title", is("Сплав на байдарках"))
                // todo  .body("views", is())
                //   .body("confirmedRequests", is())
                .body("state", is("PENDING"));

    }

    @Test
    void createEvent_whenEventDateIsEarlyThan2HoursFromNow_thanReturn409() {
        given().contentType(ContentType.JSON)
                .body("{\n" + "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" + "  \"category\": 1,\n" + "  \"description\": \"Сплав  дарит чувство обновления, феерические эмоции, яркие впечатления.\",\n" + "  \"eventDate\": \"2020-12-31 15:10:05\",\n" + "  \"location\": {\n" + "    \"lat\": 55.754167,\n" + "    \"lon\": 37.62\n" + "  },\n" + "  \"title\": \"Сплав на байдарках\" \n" + "}")
                .pathParam("userId", 1L)
                .when()
                .post(PRIVATE_EVENT)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", is("CONFLICT"))
                .body("reason", is("For the requested operation the conditions are not met."))
                .body("message", containsString("Field: eventDate. Error: должно содержать дату, " + "которая еще не наступила. Value: 2020-12-31T15:10:05"));
    }

    @Test
    void updateEvent_whenStatusIsPending_return200() {
        createUser();
        createCategory();
        createCategory();
        createEvent();

        given().contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .pathParam("eventId", 1)
                .body("{\n" + "  \"annotation\": \"Сап прогулки – это возможность увидеть Практикбург с другого ракурса\",\n" + "  \"category\": 2,\n" + "  \"description\": \"От английского SUP - Stand Up Paddle — гавайская разновидность сёрфинга.\",\n" + "  \"eventDate\": \"2024-10-11 23:10:05\",\n" + "  \"location\": {\n" + "    \"lat\": 24.754167,\n" + "    \"lon\": 24.62\n" + "  },\n" + "  \"paid\": true,\n" + "  \"participantLimit\": 24,\n" + "  \"requestModeration\": false,\n" + "  \"stateAction\": \"CANCEL_REVIEW\",\n" + "  \"title\": \"Сап прогулки по рекам и каналам\"\n" + "}")
                .when()
                .patch(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("annotation", is("Сап прогулки – это возможность увидеть Практикбург с другого ракурса"))
                .body("category.id", is(2))
                .body("category.name", containsString("Концерты"))
                .body("paid", is(true))
                .body("createdOn", notNullValue())
                .body("description", is("От английского SUP - Stand Up Paddle — гавайская разновидность сёрфинга."))
                .body("eventDate", is("2024-10-11 23:10:05"))
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("initiator.name", is("Иван Петров"))
                .body("location.lat", equalTo(Float.parseFloat("24.754167")))
                .body("location.lon", equalTo(Float.parseFloat("24.62")))
                .body("participantLimit", is(24))
                .body("publishedOn", nullValue())
                .body("requestModeration", is(false))
                .body("title", is("Сап прогулки по рекам и каналам"))
                // todo  .body("views", is())
                //   .body("confirmedRequests", is())
                .body("state", is("CANCELED"));

    }

    @Test
    void updateEvent_whenStatusIsCanceled_return200() {
        createUser();
        createCategory();
        createEvent();

        given().contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .pathParam("eventId", 1)
                .body("{\n" + "  \"stateAction\": \"CANCEL_REVIEW\"\n" + "}")
                .when()
                .patch(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("state", is("CANCELED"));

        given().contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .pathParam("eventId", 1)
                .body("{\n" + "  \"description\": \"От английского SUP - Stand Up Paddle — гавайская разновидность сёрфинга.\",\n" + "  \"eventDate\": \"2024-10-11 23:10:05\",\n" + "  \"location\": {\n" + "    \"lat\": 24.754167,\n" + "    \"lon\": 24.62\n" + "  },\n" + "  \"paid\": true,\n" + "  \"participantLimit\": 24,\n" + "  \"requestModeration\": false,\n" + "  \"stateAction\": \"SEND_TO_REVIEW\",\n" + "  \"title\": \"Сап прогулки по рекам и каналам\"\n" + "}")
                .when()
                .patch(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("annotation", is("Сплав на байдарках похож на полет."))
                .body("category.id", is(1))
                .body("category.name", containsString("Концерты"))
                .body("paid", is(true))
                .body("createdOn", notNullValue())
                .body("description", is("От английского SUP - Stand Up Paddle — гавайская разновидность сёрфинга."))
                .body("eventDate", is("2024-10-11 23:10:05"))
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("initiator.name", is("Иван Петров"))
                .body("location.lat", equalTo(Float.parseFloat("24.754167")))
                .body("location.lon", equalTo(Float.parseFloat("24.62")))
                .body("participantLimit", is(24))
                .body("publishedOn", nullValue())
                .body("requestModeration", is(false))
                .body("title", is("Сап прогулки по рекам и каналам"))
                // todo  .body("views", is())
                //   .body("confirmedRequests", is())
                .body("state", is("PENDING"));
    }

    @Test
    void updateEvent_whenStatusIsPublished_return409() {
        createUser();
        createCategory();
        createEvent();

        given().contentType(ContentType.JSON)
                .pathParam("eventId", 1)
                .body("{\n" + "  \"stateAction\": \"PUBLISH_EVENT\"\n" + "}")
                .when()
                .patch(ADMIN_EVENT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("state", is("PUBLISHED"))
                .body("publishedOn", notNullValue());

        given().contentType(ContentType.JSON)
                .pathParam("userId", 1)
                .pathParam("eventId", 1)
                .body("{\n" + "  \"stateAction\": \"CANCEL_REVIEW\"\n" + "}")
                .when()
                .patch(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("reason", is("For the requested operation the conditions are not met."))
                .body("message", is("Only pending or canceled events can be changed"));

        given().pathParam("userId", 1)
                .pathParam("eventId", 1)
                .when()
                .get(PRIVATE_EVENT + "/{eventId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("initiator.id", is(1))
                .body("state", is("PUBLISHED"))
                .body("publishedOn", notNullValue());
    }
}