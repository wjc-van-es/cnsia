package com.polarbookshop.catalogservice.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example of a pure Unit test to test the validation configuration of all the Book's attributes.
 * The {@link Validator} dependency is needed to help with these tests.
 * No mocked objects are needed.
 */
public class BookValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setup(){
        ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book =  Book.of("1234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnDefinedIncorrectlyThenValidationFails() {
        var book = Book.of("a234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().findFirst().get().getMessage()).isEqualTo("The ISBN format must be valid.");
    }

}
