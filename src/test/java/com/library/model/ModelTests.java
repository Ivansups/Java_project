package com.library.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ModelTests {

    @Test
    void bookIsAvailableDependsOnCopies() {
        Book book = new Book("Test", "Author", 2020, 1);
        book.setCopiesAvailable(0);

        assertThat(book.isAvailable()).isFalse();

        book.setCopiesAvailable(2);
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void loanIsOverdueWhenActiveAndPastDueDate() {
        Loan loan = new Loan(1, 1, LocalDate.now().minusDays(1));

        assertThat(loan.isOverdue()).isTrue();

        loan.setStatus("RETURNED");
        assertThat(loan.isOverdue()).isFalse();
    }

    @Test
    void readerIsActiveChecksStatus() {
        Reader reader = new Reader("Имя", "CARD-10", "0", "mail", "addr");
        reader.setStatus("BLOCKED");

        assertThat(reader.isActive()).isFalse();

        reader.setStatus("ACTIVE");
        assertThat(reader.isActive()).isTrue();
    }
}

