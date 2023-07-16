package com.polarbookshop.catalogservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void whenBookToCreateAlreadyExistsThenThrows() {
        // given
        var bookIsbn = "1234561232";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90);
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);

        //when addBookToCatalog is called then BookAlreadyExistsException is thrown
        String expectedMessage = "A book with ISBN " + bookIsbn + " already exists.";

        // The AssertJ variety
        assertThatThrownBy(() -> bookService.addBookToCatalog(bookToCreate))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage(expectedMessage);

        // The JUnit 5 variety
        BookAlreadyExistsException thrown = assertThrows(BookAlreadyExistsException.class,
                () -> bookService.addBookToCatalog(bookToCreate));
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void whenBookToReadDoesNotExistThenThrows() {
        var book = Book.of("1234561232", "The meaning of nothing", "B. McNair", 24.95);
        when(bookRepository.existsByIsbn(book.isbn())).thenReturn(true);
        assertThatThrownBy(() -> bookService.addBookToCatalog(book))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("A book with ISBN " + book.isbn() + " already exists.");
    }

}
