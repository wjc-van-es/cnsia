package com.polarbookshop.catalogservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
        var book = new Book(isbn, "The meaning of nothing", "B. McNair", 24.95);
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

}
