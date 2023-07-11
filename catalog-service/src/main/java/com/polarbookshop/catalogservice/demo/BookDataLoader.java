package com.polarbookshop.catalogservice.demo;

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
		var book1 = new Book("1234567893", "Following the Polestar", "Hank P***star", 9.99);
		var book2 = new Book("1234567894", "Pole night, out of sight", "Scott Alpine", 12.99);
		bookRepository.save(book1);
		bookRepository.save(book2);
	}

}
