package com.polarbookshop.catalogservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Because we want to be able to include the ability of the Jackson ObjectMapper to our test we need to modify
 * the Configuration of our vanilla {@link BookControllerMvcTests}
 * For this we added the @ExtendWith(SpringExtension.class) annotation then we are able to inject the
 * {@link #objectMapper}
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class BookControllerMvcExtendedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";
        String msg = "The book with ISBN " + isbn + " was not found.";
        given(bookService.viewBookDetails(isbn)).willThrow(new BookNotFoundException(isbn));
        mockMvc
                .perform(
                        get("/books/" + isbn)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer header.body.sig"))
                .andDo(print())
                .andExpect(status().isNotFound()) // The right HTTP status 404 is returned
                .andExpect(jsonPath("$").value(msg)); //The response body contains the message from the ex
    }

    @Test
    void whenIsbnIsInvalidThenShouldReturn400() throws Exception {
        var isbn = "1234561232";
        var msg = "A book with ISBN " + isbn + " already exists.";
        var book = Book.of(isbn, "The meaning of nothing", "B. McNair", 24.95, null);
        String content = objectMapper.writeValueAsString(book);
        given(bookService.addBookToCatalog(book)).willThrow(new BookAlreadyExistsException(isbn));
        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity()) // The right HTTP status 422 is returned
                .andExpect(jsonPath("$").value(msg)); //The response body contains the message from the ex
    }

    @Test
    void testDeserializeMissingMandatoryField() throws Exception {
        var content = """
                {
                    "isbn": "1234567890",
                    "author": "Author",
                    "price": 9.90,
                    "version": 0
                }
                """;


        Book book = objectMapper.readValue(content, Book.class);
        assertNotNull(book);
        assertEquals("Author", book.author());

        //This proves validation is not done during JSON deserialization
        ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        List<String> constraintViolationMessages = violations.stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.toList());
        assertThat(constraintViolationMessages)
                .contains("The book title must be defined.");

    }

}
