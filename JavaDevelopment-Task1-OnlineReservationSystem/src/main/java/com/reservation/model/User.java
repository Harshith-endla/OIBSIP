package com.reservation.model;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;

    public User(
            int userId,
            String fullName,
            String email,
            String phone,
            String passwordHash) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
