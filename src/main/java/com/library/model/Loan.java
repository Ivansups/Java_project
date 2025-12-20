package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private int id;
    private int bookId;
    private int readerId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private String status; // ACTIVE, RETURNED, OVERDUE

    public Loan() {
        this.status = "ACTIVE";
        this.issueDate = LocalDate.now();
    }

    public Loan(int bookId, int readerId, LocalDate dueDate) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.dueDate = dueDate;
        this.status = "ACTIVE";
        this.issueDate = LocalDate.now();
    }

    public Loan(int id, int bookId, int readerId, LocalDate issueDate, 
                LocalDate returnDate, LocalDate dueDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.readerId = readerId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isOverdue() {
        return isActive() && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return id == loan.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

