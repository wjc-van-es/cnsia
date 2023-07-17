package com.polarbookshop.catalogservice.domain;


import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

public record Book(

        @Id
        Long id,

        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(
                regexp = "^([0-9]{10}|[0-9]{13})$",
                message ="The ISBN format must be valid."
        )
        String isbn,
        @NotBlank(message = "The book title must be defined.")
        String title,
        @NotBlank(message = "The book author must be defined.")
        String author,
        @NotNull(message = "The book price must be defined.")
        @Positive(
                message = "The book price must be greater than zero."
        )
        Double price,

        String publisher,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version

){

        /**
         * Factory method for creating a new Book assumed to be NOT in the DB already
         * Hence, for insert purposes
         * @param isbn
         * @param title
         * @param author
         * @param price
         * @return
         */
        public static Book of(String isbn, String title, String author, Double price, String publisher) {
                return new Book(null, isbn, title, author, price, publisher, null, null, 0);
        }


        // We have overwritten equals and hashcode to omit the technical JDBC fields and compare objects
        // only by their natural key, not by their DB PK identity, version and auditing fields
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Book book = (Book) o;

                if (!isbn.equals(book.isbn)) return false;
                if (!title.equals(book.title)) return false;
                if (!author.equals(book.author)) return false;
                return price.equals(book.price);
        }

        @Override
        public int hashCode() {
                int result = isbn.hashCode();
                result = 31 * result + title.hashCode();
                result = 31 * result + author.hashCode();
                result = 31 * result + price.hashCode();
                return result;
        }
}
