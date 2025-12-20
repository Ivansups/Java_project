package com.library.model;

import java.util.Objects;

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private int copiesTotal;
    private int copiesAvailable;

    public Book() {
    }

    public Book(String title, String author, int year, int copiesTotal) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.copiesTotal = copiesTotal;
        this.copiesAvailable = copiesTotal;
    }

    public Book(int id, String title, String author, int year, int copiesTotal, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.copiesTotal = copiesTotal;
        this.copiesAvailable = copiesAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCopiesTotal() {
        return copiesTotal;
    }

    public void setCopiesTotal(int copiesTotal) {
        this.copiesTotal = copiesTotal;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title + " - " + author + " (" + year + ")";
    }
}

