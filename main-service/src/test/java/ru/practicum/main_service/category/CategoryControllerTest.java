package ru.practicum.main_service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.main_service.controller.admin.CategoryAdminController;
import ru.practicum.main_service.dto.CategoryRequest;
import ru.practicum.main_service.dto.CategoryResponse;
import ru.practicum.main_service.errorHandler.ExceptionApiHandler;
import ru.practicum.main_service.service.CategoryService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryAdminController.class)
public class CategoryControllerTest {

    public static final String PATH = "http://localhost:8080/admin/categories";
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private CategoryAdminController categoryController;
    @Autowired
    private ExceptionApiHandler handler;
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void createCategory_whenRequestValid_thanReturn201() throws Exception {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Festival");

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Festival");

        doReturn(categoryResponse)
                .when(categoryService).createCategory(any(CategoryRequest.class));

        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"name\": \"Festival\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Festival")));

        verify(categoryService, times(1))
                .createCategory(categoryRequest);
    }

    @Test
    void createCategory_whenBodyIsEmpty_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{ }")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: name. Error: must not be blank. " +
                        "Value: null")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void createCategory_whenNameIsBlank_thanReturn() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"name\": \"  \"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Field: name. Error: must not be blank. Value:   ")))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void updateCategory_whenRequestValid_thanReturn200() throws Exception {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Festival NEW");

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Festival NEW");

        doReturn(categoryResponse)
                .when(categoryService).updateCategory(anyLong(), any(CategoryRequest.class));

        mvc.perform(MockMvcRequestBuilders
                        .patch(PATH + "/{catId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\n" +
                                "  \"name\": \"Festival NEW\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Festival NEW")));

        verify(categoryService, times(1))
                .updateCategory(1L, categoryRequest);
    }

    @Test
    void deleteCategory_whenRequestValid_thanReturn200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete(PATH + "/{catId}", 1))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }
}
