package com.library.database;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.testutil.DatabaseTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagerLoansTest {

    private DatabaseManager dbManager;
    private int bookId;
    private int readerId;

    @BeforeEach
    void setUp() {
        dbManager = DatabaseTestHelper.setupTestDatabase();

        // Создаем изолированные данные для выдач
        dbManager.addBook(new Book("Loanable Book", "QA Author", 2024, 2));
        dbManager.addReader(new Reader("Loan Reader", "CARD-LOAN", "+7-900-000-00-01", "loan@test.ru", "Address"));
        bookId = dbManager.searchBooks("Loanable Book").get(0).getId();
        readerId = dbManager.getReaderByCardNumber("CARD-LOAN").getId();
    }

    @Test
    void addLoanReducesAvailableCopies() {
        int availableBefore = dbManager.getBookById(bookId).getCopiesAvailable();
        Loan loan = new Loan(bookId, readerId, LocalDate.now().plusDays(7));

        dbManager.addLoan(loan);

        int availableAfter = dbManager.getBookById(bookId).getCopiesAvailable();
        assertThat(availableAfter).isEqualTo(availableBefore - 1);
    }

    @Test
    void returnLoanRestoresAvailabilityAndStatus() {
        Loan loan = new Loan(bookId, readerId, LocalDate.now().plusDays(5));
        dbManager.addLoan(loan);
        int loanId = dbManager.getAllLoans().get(0).getId();

        dbManager.returnLoan(loanId);

        Loan returned = dbManager.getLoanById(loanId);
        assertThat(returned.getStatus()).isEqualTo("RETURNED");
        assertThat(dbManager.getBookById(bookId).getCopiesAvailable()).isEqualTo(2);
    }

    @Test
    void extendLoanUpdatesDueDate() {
        Loan loan = new Loan(bookId, readerId, LocalDate.now().plusDays(3));
        dbManager.addLoan(loan);
        List<Loan> loans = dbManager.getLoansByReaderId(readerId);
        int loanId = loans.get(0).getId();

        LocalDate newDueDate = LocalDate.now().plusDays(14);
        dbManager.extendLoan(loanId, newDueDate);

        assertThat(dbManager.getLoanById(loanId).getDueDate()).isEqualTo(newDueDate);
    }
}

