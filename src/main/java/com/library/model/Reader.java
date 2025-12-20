package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reader {
    private int id;
    private String fullName;
    private String cardNumber;
    private LocalDate registrationDate;
    private String phone;
    private String email;
    private String address;
    private String status; // ACTIVE, BLOCKED

    public Reader() {
        this.status = "ACTIVE";
        this.registrationDate = LocalDate.now();
    }

    public Reader(String fullName, String cardNumber, String phone, String email, String address) {
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.status = "ACTIVE";
        this.registrationDate = LocalDate.now();
    }

    public Reader(int id, String fullName, String cardNumber, LocalDate registrationDate, 
                  String phone, String email, String address, String status) {
        this.id = id;
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.registrationDate = registrationDate;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return id == reader.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fullName + " (" + cardNumber + ")";
    }
}

