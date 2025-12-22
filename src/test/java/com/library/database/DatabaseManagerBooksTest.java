package com.library.database;

import com.library.model.Book;
import com.library.testutil.DatabaseTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagerBooksTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        dbManager = DatabaseTestHelper.setupTestDatabase();
    }

    @Test
    void shouldLoadSampleBooksOnInit() {
        List<Book> books = dbManager.getAllBooks();
        assertThat(books).isNotEmpty();
    }

    @Test
    void shouldAddBookAndIncreaseTotals() {
        int totalBefore = dbManager.getTotalBooks();
        Book book = new Book("Test Driven Development", "Kent Beck", 2003, 5);

        dbManager.addBook(book);

        assertThat(dbManager.getTotalBooks()).isEqualTo(totalBefore + 1);
        assertThat(dbManager.searchBooks("Test Driven Development"))
            .anySatisfy(found -> assertThat(found.getCopiesAvailable()).isEqualTo(5));
    }

    @Test
    void shouldFindBookByTitleOrAuthor() {
        dbManager.addBook(new Book("Domain-Driven Design", "Eric Evans", 2003, 3));

        List<Book> byTitle = dbManager.searchBooks("Domain-Driven");
        List<Book> byAuthor = dbManager.searchBooks("Eric Evans");

        assertThat(byTitle).isNotEmpty();
        assertThat(byAuthor).isNotEmpty();
    }

    @Test
    void shouldGetBookById() {
        dbManager.addBook(new Book("JUnit in Action", "Sam Brannen", 2020, 4));
        int id = dbManager.searchBooks("JUnit in Action").get(0).getId();

        Book loaded = dbManager.getBookById(id);

        assertThat(loaded).isNotNull();
        assertThat(loaded.getTitle()).isEqualTo("JUnit in Action");
    }

    @Test
    void shouldUpdateBookFields() {
        dbManager.addBook(new Book("Clean Code", "Robert C. Martin", 2008, 2));
        Book toUpdate = dbManager.searchBooks("Clean Code").get(0);

        toUpdate.setCopiesAvailable(1);
        toUpdate.setCopiesTotal(3);
        toUpdate.setYear(2009);
        dbManager.updateBook(toUpdate);

        Book updated = dbManager.getBookById(toUpdate.getId());
        assertThat(updated.getCopiesAvailable()).isEqualTo(1);
        assertThat(updated.getCopiesTotal()).isEqualTo(3);
        assertThat(updated.getYear()).isEqualTo(2009);
    }

    @Test
    void shouldDeleteBook() {
        dbManager.addBook(new Book("Refactoring UI", "Adam Wathan", 2018, 2));
        Book added = dbManager.searchBooks("Refactoring UI").get(0);

        dbManager.deleteBook(added.getId());

        assertThat(dbManager.getBookById(added.getId())).isNull();
    }
}

