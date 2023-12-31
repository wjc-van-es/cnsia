package com.polarbookshop.catalogservice.demo;

import java.util.List;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookRepository;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("testdata")
public class BookDataLoader {

	private final BookRepository bookRepository;

	public BookDataLoader(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadBookTestData() {
		bookRepository.deleteAll();
		var book1 = Book.of("1234567893", "Following the Polestar", "Hank P***star", 9.99, "Polarsophia");
		var book2 = Book.of("1234567894", "Pole night, out of sight", "Scott Alpine", 12.99, "Polarsophia");
		var book3 = Book.of("1234567895", "Obsessing over Artic thaw", "Hermione Frozen", 12.99, "Cornucopia");
		bookRepository.saveAll(List.of(book1, book2, book3));
	}

}
