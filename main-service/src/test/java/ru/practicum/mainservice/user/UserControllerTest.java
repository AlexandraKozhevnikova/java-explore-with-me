package ru.practicum.mainservice.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.mainservice.controller.admin.UserController;
import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.UserResponse;
import ru.practicum.mainservice.errorHandler.ExceptionApiHandler;
import ru.practicum.mainservice.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    public static final String PATH = "http://localhost:8080/admin/users";
    @MockBean
    private UserService userService;
    @Autowired
    private UserController userController;
    @Autowired
    private ExceptionApiHandler handler;
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void createUser_whenRequestValid_thanReturn201() throws Exception {
        doReturn(getUserResponse())
                .when(userService).createUser(any(NewUserRequest.class));

        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"email\": \"ivan.petrov@practicummail.ru\",\n" +
                                "  \"name\": \"Иван Петров\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Петров")))
                .andExpect(jsonPath("$.email", is("ivan.petrov@practicummail.ru")));

        verify(userService, times(1))
                .createUser(getNewUserRequest());
    }

    @Test
    void createUser_whenBodyIsEmpty_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message",
                        anyOf(is("Field: email. Error: must not be blank. Value: null"),
                                is("Field: name. Error: must not be blank. Value: null"))))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void createUser_whenEmailIsNotCorrect_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"email\": \"ivan.petrov\",\n" +
                                "  \"name\": \"Иван Петров\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: email. Error: must be a well-formed " +
                        "email address. Value: ivan.petrov")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void createUser_whenNameIsBlank_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"email\": \"ivan@ya.ru\",\n" +
                                "  \"name\": \"  \"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: name. Error: must not be blank. Value: ")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void getUsers_whenRequestValidWithAllParams_thanReturnUserList() throws Exception {
        doReturn(List.of(getUserResponse()))
                .when(userService).getUsers(anyList(), anyInt(), anyInt());

        mvc.perform(MockMvcRequestBuilders
                        .get(PATH)
                        .param("ids", "1")
                        .param("ids", "2")
                        .param("from", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Иван Петров")))
                .andExpect(jsonPath("$[0].email", is("ivan.petrov@practicummail.ru")));

        verify(userService, times(1))
                .getUsers(List.of(1L, 2L), 1, 5);
    }

    @Test
    void getUsers_whenRequestWithoutParams_thanReturnUserListWithDefaultParams() throws Exception {
        doReturn(List.of(getUserResponse()))
                .when(userService).getUsers(any(), anyInt(), anyInt());

        mvc.perform(MockMvcRequestBuilders
                        .get(PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Иван Петров")))
                .andExpect(jsonPath("$[0].email", is("ivan.petrov@practicummail.ru")));

        verify(userService, times(1))
                .getUsers(null, 0, 10);
    }

    @Test
    void getUsers_whenIdsIsEmptyList_thanReturnUserListWithDefaultParams() throws Exception {
        doReturn(List.of(getUserResponse()))
                .when(userService).getUsers(anyList(), anyInt(), anyInt());

        mvc.perform(MockMvcRequestBuilders
                        .get(PATH)
                        .param("ids", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Иван Петров")))
                .andExpect(jsonPath("$[0].email", is("ivan.petrov@practicummail.ru")));

        verify(userService, times(1))
                .getUsers(Collections.EMPTY_LIST, 0, 10);
    }

    @Test
    void getUsers_whenParamsHaveOtherType_thanReturn400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(PATH)
                        .param("ids", "abc")
                        .param("ids", "df")
                        .param("from", "from")
                        .param("size", "size")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Failed to convert value of type 'java.lang.String[]'" +
                        " to required type 'java.util.List'; nested exception is java.lang.NumberFormatException: For input " +
                        "string: \"abc\"")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    private NewUserRequest getNewUserRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Петров");
        request.setEmail("ivan.petrov@practicummail.ru");
        return request;
    }

    private UserResponse getUserResponse() {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Иван Петров");
        response.setEmail("ivan.petrov@practicummail.ru");
        return response;
    }
}
