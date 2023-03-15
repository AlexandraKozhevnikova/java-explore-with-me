package ru.practicum.main_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.main_service.config.LocalDataTimeRegisterConfig;
import ru.practicum.main_service.controller.registred_user.EventPrivateController;
import ru.practicum.main_service.errorHandler.ExceptionApiHandler;
import ru.practicum.main_service.service.BillService;
import ru.practicum.main_service.service.EventService;
import ru.practicum.main_service.service.RequestService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventPrivateController.class)
@Import(LocalDataTimeRegisterConfig.class)
public class EventControllerTest {

    MockMvc mvc;
    @Autowired
    ExceptionApiHandler handler;
    @Autowired
    EventPrivateController eventPrivateController;
    @Autowired
    ObjectMapper jsonMapper;
    @MockBean
    EventService eventService;
    @MockBean
    RequestService requestService;
    @MockBean
    BillService billService;
    @Autowired
    JavaTimeModule module;

    @BeforeEach
    public void setUp() {
        jsonMapper.registerModule(module);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(jsonMapper);

        mvc = MockMvcBuilders.standaloneSetup(eventPrivateController)
                .setControllerAdvice(handler)
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }


    @Test
    void createEvent_whenEventDateIsNull_thanReturn409() throws Exception {
        String requestBody = "{\n" +
                "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                "  \"category\": 2,\n" +
                "  \"description\": \"Сплав на байдарках похож на полет. На спокойной воде — это парение. На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                "  \"eventDate\": null,\n" +
                "  \"location\": {\n" +
                "    \"lat\": 55.754167,\n" +
                "    \"lon\": 37.62\n" +
                "  },\n" +
                "  \"paid\": true,\n" +
                "  \"participantLimit\": 10,\n" +
                "  \"requestModeration\": false,\n" +
                "  \"title\": \"Сплав на байдарках\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/users/{userId}/events", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: eventDate. Error: must not be null. Value: null")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void createEvent_whenBodyWithoutAnnotation_thanReturn400() throws Exception {
        String requestBody = "{\n" +
                "  \"annotation\": null,\n" +
                "  \"category\": 2,\n" +
                "  \"description\": \"Сплав на байдарках похож на полет. На спокойной воде — это парение. На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                "  \"eventDate\": \"2024-12-31 15:10:05\",\n" +
                "  \"location\": {\n" +
                "    \"lat\": 55.754167,\n" +
                "    \"lon\": 37.62\n" +
                "  },\n" +
                "  \"paid\": true,\n" +
                "  \"participantLimit\": 10,\n" +
                "  \"requestModeration\": false,\n" +
                "  \"title\": \"Сплав на байдарках\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/users/{userId}/events", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: annotation. Error: must not be blank." +
                        " Value: null")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void createEvent_whenTitleIsLessThan3chars_return400() throws Exception {
        String requestBody = "{\n" +
                "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                "  \"category\": 2,\n" +
                "  \"description\": \"Сплав на байдарках похож на полет. На спокойной воде — это парение. На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                "  \"eventDate\": \"2024-12-31 15:10:05\",\n" +
                "  \"location\": {\n" +
                "    \"lat\": 55.754167,\n" +
                "    \"lon\": 37.62\n" +
                "  },\n" +
                "  \"paid\": true,\n" +
                "  \"participantLimit\": 10,\n" +
                "  \"requestModeration\": false,\n" +
                "  \"title\": \"ОК\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/users/{userId}/events", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: title. Error: size must be between 3 and " +
                        "120. Value: ОК")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    private String getStringNewEventRequest() {
        return "{\n" +
                "  \"annotation\": \"Сплав на байдарках похож на полет.\",\n" +
                "  \"category\": 2,\n" +
                "  \"description\": \"Сплав на байдарках похож на полет. На спокойной воде — это парение. На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.\",\n" +
                "  \"eventDate\": \"2024-12-31 15:10:05\",\n" +
                "  \"location\": {\n" +
                "    \"lat\": 55.754167,\n" +
                "    \"lon\": 37.62\n" +
                "  },\n" +
                "  \"paid\": true,\n" +
                "  \"participantLimit\": 10,\n" +
                "  \"requestModeration\": false,\n" +
                "  \"title\": \"Сплав на байдарках\"\n" +
                "}";
    }
}
