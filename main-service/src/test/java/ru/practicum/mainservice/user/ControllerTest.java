package ru.practicum.mainservice.user;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.mainservice.controller.UserController;
import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.UserResponse;
import ru.practicum.mainservice.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class ControllerTest {
    public static final String PATH = "http://localhost:8080/admin/users";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

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
    @Disabled
    void createUser_whenWithoutBody_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.reason", is("Иван Петров")))
            .andExpect(jsonPath("$.message", is("ivan.petrov@practicummail.ru")))
            .andExpect(jsonPath("$.timestamp", is("ivan.petrov@practicummail.ru")));
    }

    @Test
    void createUser_whenBodyIsEmpty_thanReturn() {

    }

    @Test
    void createUser_whenEmailIsNotCorrect_thanReturn() {

    }

    @Test
    void createUser_whenNameIsBlank_thanReturn() {

    }

//
//
//
//    \
//

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
