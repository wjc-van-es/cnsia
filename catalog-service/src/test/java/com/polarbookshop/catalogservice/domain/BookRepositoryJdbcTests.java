package com.polarbookshop.catalogservice.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.*;

import com.polarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests {

    private final Logger logger = LoggerFactory.getLogger(BookRepositoryJdbcTests.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findAllBooksInsertedByFlyway_V5__script(){
        Iterable<Book> actualBooks = bookRepository.findAll();
        List<Book> books = StreamSupport
                .stream(actualBooks.spliterator(), true)
                .collect(Collectors.toList());
        int size = books.size();

        // interference from com.polarbookshop.catalogservice.CatalogServiceApplicationTests may lead to 7 values
        // instead of 4. With a size > 3 we have more than the 3 from that test; either 4 or 4 + 3 = 7
        assertTrue(size > 3);
        books.forEach(book -> logger.info(book.toString()));
        Book isbn9781633438958 = books.stream()
                .filter(book -> book.isbn().equals("9781633438958"))
                .findFirst().get();
        logger.info("We retrieved {} books, one of which is\n{}", size, isbn9781633438958);
        assertNotNull(isbn9781633438958);
        assertEquals("Martin Štefanko and Jan Martiška", isbn9781633438958.author());
        assertEquals("Artic & Antartic Quarkus in Action", isbn9781633438958.title());
        assertEquals(19.99, isbn9781633438958.price());
        assertEquals("Manning Publications Co.", isbn9781633438958.publisher());
    }

    @Test
    void findAllBooks() {
        var book1 = Book.of("1234561235", "Title", "Author", 12.90, "Polarsophia");
        var book2 = Book.of("1234561236", "Another Title", "Author", 12.90, null);
        jdbcAggregateTemplate.insert(book1);
        jdbcAggregateTemplate.insert(book2);

        Iterable<Book> actualBooks = bookRepository.findAll();

        assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
                .filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()))
                .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        jdbcAggregateTemplate.insert(book);

        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }

    @Test
    void findBookByIsbnWhenNotExisting() {
        Optional<Book> actualBook = bookRepository.findByIsbn("1234561238");
        assertThat(actualBook).isEmpty();
    }

    @Test
    void existsByIsbnWhenExisting() {
        var bookIsbn = "1234561239";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90, null);
        jdbcAggregateTemplate.insert(bookToCreate);

        boolean existing = bookRepository.existsByIsbn(bookIsbn);

        assertThat(existing).isTrue();
    }

    @Test
    void existsByIsbnWhenNotExisting() {
        boolean existing = bookRepository.existsByIsbn("1234561240");
        assertThat(existing).isFalse();
    }

    @Test
    void deleteByIsbn() {
        var bookIsbn = "1234561241";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        var persistedBook = jdbcAggregateTemplate.insert(bookToCreate);

        bookRepository.deleteByIsbn(bookIsbn);

        assertThat(jdbcAggregateTemplate.findById(persistedBook.id(), Book.class)).isNull();
    }

}
